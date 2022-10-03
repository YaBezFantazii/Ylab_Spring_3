package com.edu.ulab.app.web.request.create;

import lombok.Data;



@Data
public class BookRequest {
    private String title;
    private String author;
    private long pageCount;
}
