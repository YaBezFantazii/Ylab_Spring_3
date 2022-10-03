package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestExceptionUpdate;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (Objects.isNull(userDto)){throw new NotFoundException("userDto is null");}
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if (Objects.isNull(userDto) || Objects.isNull(userDto.getId())){
            throw new BadRequestExceptionUpdate(userDto);
        }
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("User not found"));
        Person updatedUser = userRepository.save(user);
        log.info("Updated user: {}", updatedUser);
        return userMapper.personToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        if (Objects.isNull(id)){throw new NotFoundException("id is null");}
        Person person = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.personToUserDto(person);
    }

    @Override
    public void deleteUserById(Long id) {
        if (Objects.isNull(id)){throw new NotFoundException("id is null");}
        userRepository.findById(id).orElseThrow(()->new NotFoundException("User not found"));
        userRepository.deleteById(id);
    }
}
