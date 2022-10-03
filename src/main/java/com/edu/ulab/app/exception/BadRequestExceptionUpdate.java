package com.edu.ulab.app.exception;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;

public class BadRequestExceptionUpdate extends RuntimeException{

    public BadRequestExceptionUpdate(BookDto bookDto) {
        super("bookDto or bookDto.id is null: "+ bookDto);
    }

    public BadRequestExceptionUpdate(UserDto userDto) {
        super("userDto or userDto.id is null: "+ userDto);
    }
}
