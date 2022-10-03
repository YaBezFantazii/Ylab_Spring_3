package com.edu.ulab.app.web.response;

import com.edu.ulab.app.dto.BookDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBookResponse {
    private Long userId;
    private String fullName;
    private String title;
    private int age;
    //    private List<Long> booksIdList;
    private List<BookDto> bookList;
}
