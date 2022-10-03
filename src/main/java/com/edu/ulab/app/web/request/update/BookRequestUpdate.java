package com.edu.ulab.app.web.request.update;

import lombok.Data;



@Data
public class BookRequestUpdate {
    private long id;
    private String title;
    private String author;
    private long pageCount;
}
