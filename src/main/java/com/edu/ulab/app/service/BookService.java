package com.edu.ulab.app.service;

import java.util.List;
import com.edu.ulab.app.dto.BookDto;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    BookDto updateBook(BookDto bookDto);

    List<BookDto> getBookById(Long id);

    void deleteBookById(Long id);
}
