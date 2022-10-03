package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestExceptionUpdate;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @Spy
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;
    @Mock
    UserRepository userRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(userRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Попытка создания пустой книги")
    void saveBookEmpty_Test() {
        //given
        BookDto bookDto = new BookDto();
        bookDto.setAuthor(null);
        bookDto.setPageCount(0);
        bookDto.setTitle(null);
        //then
        assertNull(bookService.createBook(bookDto));
    }

    @Test
    @DisplayName("Обновление книги")
    void updateBook_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book checkBook = new Book();
        checkBook.setId(1L);
        checkBook.setPerson(person);
        checkBook.setAuthor("test author");
        checkBook.setTitle("test title");
        checkBook.setPageCount(1000);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(userRepository.findById(bookDto.getUserId())).thenReturn(Optional.of(person));
        when(bookRepository.findByIdAndPersonId( book.getId(), book.getPerson().getId())).thenReturn(checkBook);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.updateBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals("test title", bookDtoResult.getTitle());
    }

    @Test
    @DisplayName("Обновление (Создание) книги, если в ней не указан id (id=0)")
    void updateBookNoId_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);

        BookDto bookDtoNoId = new BookDto();
        bookDtoNoId.setId(0L);
        bookDtoNoId.setUserId(1L);
        bookDtoNoId.setAuthor("test author");
        bookDtoNoId.setTitle("test title");
        bookDtoNoId.setPageCount(1000);

        Book bookNoId = new Book();
        bookNoId.setPerson(person);
        bookNoId.setAuthor("test author");
        bookNoId.setTitle("test title");
        bookNoId.setPageCount(1000);

        Book savedBookNoId = new Book();
        savedBookNoId.setId(1L);
        savedBookNoId.setPerson(person);
        savedBookNoId.setAuthor("test author");
        savedBookNoId.setTitle("test title");
        savedBookNoId.setPageCount(1000);

        BookDto bookDtoNoIdCheck = new BookDto();
        bookDtoNoIdCheck.setUserId(1L);
        bookDtoNoIdCheck.setId(1L);
        bookDtoNoIdCheck.setAuthor("test author");
        bookDtoNoIdCheck.setTitle("test title");
        bookDtoNoIdCheck.setPageCount(1000);

        //when

        when(bookMapper.bookDtoToBook(bookDtoNoId)).thenReturn(bookNoId);
        when(bookRepository.save(bookNoId)).thenReturn(savedBookNoId);
        when(userRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(bookMapper.bookToBookDto(savedBookNoId)).thenReturn(bookDtoNoIdCheck);
        when(bookService.createBook(bookDtoNoId)).thenReturn(bookDtoNoIdCheck);

        //then
        BookDto bookDtoResult = bookService.updateBook(bookDtoNoId);
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Получение книг по id пользователя")
    // get all
    void getBooks_Test() {
        // given
        Person person = new Person();
        person.setId(1L);

        Book book1 = new Book();
        book1.setId(1L);
        book1.setPageCount(1000);
        book1.setTitle("test title1");
        book1.setAuthor("test author1");
        book1.setPerson(person);

        Book book2 = new Book();
        book2.setId(1L);
        book2.setPageCount(2000);
        book2.setTitle("test title2");
        book2.setAuthor("test author2");
        book2.setPerson(person);

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(1L);
        bookDto1.setUserId(1L);
        bookDto1.setAuthor("test author1");
        bookDto1.setTitle("test title1");
        bookDto1.setPageCount(1000);

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setUserId(1L);
        bookDto2.setAuthor("test author2");
        bookDto2.setTitle("test title2");
        bookDto2.setPageCount(2000);

        List<Book> books = Arrays.asList(book1,book2);
        List<BookDto> booksDto = Arrays.asList(bookDto1,bookDto2);

        // when
        when(bookRepository.findByPersonId(person.getId())).thenReturn(books);
        when(bookMapper.bookToBookDto(book1)).thenReturn(bookDto1);
        when(bookMapper.bookToBookDto(book2)).thenReturn(bookDto2);

        // then
        assertEquals(booksDto , bookService.getBookById(person.getId()));
    }

    // delete
    @Test
    @DisplayName("Удаление книги.")
    void deleteBook_Test() {
        // given
        Person person = new Person();
        person.setId(1L);
        // when
        bookService.deleteBookById(person.getId());
        // then
        verify(bookRepository).deleteByPersonId(person.getId());

    }

    @Test
    @DisplayName("Создание книги c null. Должно выбросить ошибку")
    void saveBookWithNull_Test(){
        assertThatThrownBy(() -> bookService.createBook(null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Обновление книги c null или id=null. Должно выбросить ошибку")
    void updateBookWithNull_Test(){
        assertThatThrownBy(() -> bookService.updateBook(null))
                .isInstanceOf(BadRequestExceptionUpdate.class);

        BookDto bookDto = new BookDto();
        assertThatThrownBy(() -> bookService.updateBook(bookDto))
                .isInstanceOf(BadRequestExceptionUpdate.class);
    }

    @Test
    @DisplayName("Получение книг с id=null. Должно выбросить ошибку")
    void GetBooksWithNull_Test(){
        assertThatThrownBy(() -> bookService.getBookById(null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Удаление книг с id=null. Должно выбросить ошибку")
    void DeleteBooksWithNull_Test(){
        assertThatThrownBy(() -> bookService.deleteBookById(null))
                .isInstanceOf(NotFoundException.class);
    }

}
