package com.edu.ulab.app.web.request.update;

import lombok.Data;


import java.util.List;

@Data
public class UserBookRequestUpdate {
    private UserRequestUpdate userRequest;
    private List<BookRequestUpdate> bookRequests;
}
