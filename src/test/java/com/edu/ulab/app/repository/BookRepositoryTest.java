package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void findAllBadges_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);

        //Then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить книгу и автора, и создать новую " +
            "книгу у автора (id книги не указан).")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateUserAndBooks_thenAssertDmlCount() {
        //Given
        Person getPerson = userRepository.findById(1001L)
                .orElseThrow(() -> new NotFoundException("user not found"));
        Book getBook = bookRepository.findByIdAndPersonId(2002L,1001L);
        List<Book> getBooks = bookRepository.findByPersonId(1001L);
        //Then
        assertThat(getPerson.getTitle()).isNotEqualTo("reader2");
        assertThat(getBook.getTitle()).isNotEqualTo("test");
        assertThat(getBooks.size()).isEqualTo(2);

        //Given
        Person person = new Person();
        person.setId(1001L);
        person.setAge(111);
        person.setTitle("reader2");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book1 = new Book();
        book1.setId(2002L);
        book1.setAuthor("Test Author");
        book1.setTitle("test");
        book1.setPageCount(1000);
        book1.setPerson(savedPerson);

        Book book2 = new Book();
        book2.setAuthor("Test Author2");
        book2.setTitle("test2");
        book2.setPageCount(2000);
        book2.setPerson(savedPerson);

        //When
        Book result1 = bookRepository.save(book1);
        Book result2 = bookRepository.save(book2);
        getBooks = bookRepository.findByPersonId(1001L);

        //Then
        assertThat(result1.getPageCount()).isEqualTo(1000);
        assertThat(result2.getPageCount()).isEqualTo(2000);
        assertThat(savedPerson.getTitle()).isEqualTo("reader2");
        assertThat(result1.getTitle()).isEqualTo("test");
        assertThat(result2.getTitle()).isEqualTo("test2");
        assertThat(getBooks.size()).isEqualTo(3);
        assertSelectCount(5);
        assertInsertCount(1);
        assertUpdateCount(2);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книги и автора. Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void GetUserAndBooks_thenAssertDmlCount() {
        //Given
        Person person = userRepository.findById(1001L)
                .orElseThrow(() -> new NotFoundException("user not found"));
        List<Book> books = bookRepository.findByPersonId(1001L);
        //Then
        assertThat(books.get(0).getPageCount()).isEqualTo(5500);
        assertThat(books.get(1).getPageCount()).isEqualTo(6655);
        assertThat(person.getTitle()).isEqualTo("reader");
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить книги и автора. Число select должно равняться 4")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void DeleteUserAndBooks_thenAssertDmlCount() {
        List<Book> books = bookRepository.findByPersonId(1001L);
        assertThat(books.size()).isEqualTo(2);

        //Given
        bookRepository.deleteByPersonId(1001L);
        userRepository.deleteById(1001L);
        books = bookRepository.findByPersonId(1001L);

        //Then
        assertThat(books.size()).isEqualTo(0);
        assertSelectCount(4);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @DisplayName("Сохранить или обновить null книгу или null пользователя. Ошибка")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void findAllBadgesError_thenAssertDmlCount() {
        Assertions.assertThrows(Exception.class, ( ) ->
                userRepository.save(null));
        Assertions.assertThrows(Exception.class, ( ) ->
                bookRepository.save(null));
    }

    @DisplayName("Получить несуществующие книги или автора. Ошибка")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void GetUserAndBooksError_thenAssertDmlCount2() {
        Assertions.assertThrows(Exception.class, ( ) -> userRepository.findById(555L)
                .orElseThrow(() -> new NotFoundException("user not found")));
        Assertions.assertThrows(Exception.class, ( ) -> bookRepository.findById(232L)
                .orElseThrow(() -> new NotFoundException("book not found")));
        Assertions.assertThrows(Exception.class, ( ) -> userRepository.findById(null)
                .orElseThrow(() -> new NotFoundException("id is null")));
        Assertions.assertThrows(Exception.class, ( ) -> bookRepository.findById(null)
                .orElseThrow(() -> new NotFoundException("id is null")));
    }

    @DisplayName("Удалить несуществующие книги или автора. Ошибка")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void DeleteUserAndBooksError_thenAssertDmlCount2() {
        assertThrows(Exception.class, ( ) -> bookRepository.deleteById(555L));
        assertThrows(Exception.class, ( ) -> bookRepository.deleteById(null));
        assertThrows(Exception.class, ( ) -> userRepository.deleteById(555L));
        assertThrows(Exception.class, ( ) -> userRepository.deleteById(null));
    }
}
