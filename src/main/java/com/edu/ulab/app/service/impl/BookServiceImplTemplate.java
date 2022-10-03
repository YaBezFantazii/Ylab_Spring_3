package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestExceptionUpdate;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.mapper.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final UserServiceImplTemplate userServiceImplTemplate;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate, BookMapper bookMapper, UserMapper userMapper,
                                   UserServiceImplTemplate userServiceImplTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
        this.userMapper=userMapper;
        this.userServiceImplTemplate=userServiceImplTemplate;
    }
    public boolean bookIsEmpty (BookDto bookDto){
        return  bookDto.getTitle() == null && bookDto.getAuthor() == null && bookDto.getPageCount() == 0;
    }
    public Person getPersonFromBookDto (BookDto bookDto){
        return userMapper.userDtoToPerson(
                userServiceImplTemplate.getUserById( bookDto.getUserId() ));
    }

    private Book mapRowToBook(ResultSet resultSet, int rowNum) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getLong("ID"));
        book.setTitle(resultSet.getString("TITLE"));
        book.setAuthor(resultSet.getString("AUTHOR"));
        book.setPageCount(resultSet.getLong("PAGE_COUNT"));
        return book;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        if (Objects.isNull(bookDto)){throw new NotFoundException("bookDto is null");}
        if (bookIsEmpty(bookDto)) {return null;}
        final String INSERT_SQL = "INSERT INTO ULAB_EDU.BOOK(TITLE, AUTHOR, PAGE_COUNT, PERSON_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUserId());
                    return ps;
                },
                keyHolder);
        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Saved book: {}", bookDto);
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        if (Objects.isNull(bookDto) || Objects.isNull(bookDto.getId())){
            throw new BadRequestExceptionUpdate(bookDto);
        }
        // Если книга не имеет id,
        if (bookDto.getId()==0) {
            //, и она пустая, то ничего не делаем
            if (bookIsEmpty(bookDto)) {
                log.info("Don't update/saved/delete book, book is empty and not id: {}", bookDto);
                return null;
            }
            //, и она не пустая, создаем новую книгу
            return createBook(bookDto);
        }

        Book book = bookMapper.bookDtoToBook(bookDto);
        book.setPerson(getPersonFromBookDto(bookDto));
        log.info("Mapped book: {}", book);

        // Если книга пустая и имеет id, пытаемся удалить книгу из бд
        if (bookIsEmpty(bookDto)){
            final String DELETE_SQL = "DELETE FROM ULAB_EDU.BOOK WHERE ID=? AND PERSON_ID=?";
            int check = jdbcTemplate.update(DELETE_SQL,
                    book.getId(),
                    book.getPerson().getId());
            if (check==0){
                log.info("Book don't delete, not found: {}", book);
                return null;
            }
            log.info("Book delete: {}", book);
            return null;
        }

        // Обновляем книгу
        final String UPDATE_SQL = "UPDATE ULAB_EDU.BOOK SET TITLE=?, AUTHOR=?, PAGE_COUNT=?, PERSON_ID=? WHERE ID=? AND PERSON_ID=?";
        int check = jdbcTemplate.update(UPDATE_SQL,
                book.getTitle(),
                book.getAuthor(),
                book.getPageCount(),
                book.getPerson().getId(),
                book.getId(),
                book.getPerson().getId());
        // Проверка, изменили ли мы книгу в бд
        if (check==0){
            log.info("Book don't update, not found: {}", book);
            return null;
        }
        log.info("Updated book: {}", book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public List<BookDto> getBookById(Long id) {
        if (Objects.isNull(id)){throw new NotFoundException("id is null");}
        final String GET_SQL = "SELECT * FROM ULAB_EDU.BOOK WHERE PERSON_ID=?";
        List<Book> listBook = jdbcTemplate.query(
                GET_SQL,
                this::mapRowToBook,
                id);
        return listBook.stream().map(bookMapper::bookToBookDto).toList();
    }

    @Override
    public void deleteBookById(Long id) {
        if (Objects.isNull(id)){throw new NotFoundException("id is null");}
        final String DELETE_SQL = "DELETE FROM ULAB_EDU.BOOK WHERE PERSON_ID=?";
        jdbcTemplate.update(DELETE_SQL,id);
    }
}
