package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;

import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.create.UserBookRequest;
import com.edu.ulab.app.web.request.update.UserBookRequestUpdate;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    //private final UserServiceImplTemplate userService;
    //private final BookServiceImplTemplate bookService;
    private final UserServiceImpl userService;
    private final BookServiceImpl bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(//UserServiceImplTemplate userService,
                          //BookServiceImplTemplate bookService,
                          BookServiceImpl bookService,
                          UserServiceImpl userService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }
    private UserBookResponse mapUserBookResponse (UserDto userDto, List<BookDto> bookDtos){
        UserBookResponse userBookResponse = new UserBookResponse();
        userBookResponse.setUserId(userDto.getId());
        userBookResponse.setFullName(userDto.getFullName());
        userBookResponse.setTitle(userDto.getTitle());
        userBookResponse.setAge(userDto.getAge());
        userBookResponse.setBookList(bookDtos);
        return userBookResponse;
    }
    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<BookDto> bookList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .filter(Objects::nonNull)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .toList();

        log.info("Collected book : {}", bookList);

        return mapUserBookResponse(createdUser,bookList);
    }

    public UserBookResponse updateUserWithBooks(UserBookRequestUpdate userBookRequestUpdate) {
        log.info("Got user book create request: {}", userBookRequestUpdate);
        UserDto userDto = userMapper.userRequestUpdateToUserDto(userBookRequestUpdate.getUserRequest());
        log.info("Mapped user request: {}", userDto);
        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Updated user: {}", updatedUser);
        List<BookDto> bookList = userBookRequestUpdate.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestUpdateToBookDto)
                .peek(bookDto -> bookDto.setUserId(updatedUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::updateBook)
                .filter(Objects::nonNull)
                .peek(bookDto -> bookDto.setUserId(updatedUser.getId()))
                .peek(mappedBookDto -> log.info("updated book (list): {}", mappedBookDto))
                .toList();

        return mapUserBookResponse(updatedUser,bookList);
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Got user id: {}", userId);
        UserDto getUser = userService.getUserById(userId);
        log.info("Get user: {}", getUser);
        List<BookDto> bookList = bookService.getBookById(userId)
                .stream()
                .filter(Objects::nonNull)
                .peek(bookDto -> bookDto.setUserId(getUser.getId()))
                .peek(getBook -> log.info("Get book: {}", getBook))
                .toList();

        return mapUserBookResponse(getUser,bookList);
    }

    public void deleteUserWithBooks(Long userId) {
        bookService.deleteBookById(userId);
        log.info("Delete books by user id: {}", userId);
        userService.deleteUserById(userId);
        log.info("Delete user id: {}", userId);
    }

}
