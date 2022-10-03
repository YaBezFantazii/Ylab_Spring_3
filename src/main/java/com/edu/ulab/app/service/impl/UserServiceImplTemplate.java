package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestExceptionUpdate;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    private Person mapRowToPerson(ResultSet resultSet, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(resultSet.getLong("ID"));
        person.setTitle(resultSet.getString("TITLE"));
        person.setFullName(resultSet.getString("FULL_NAME"));
        person.setAge(resultSet.getInt("AGE"));
        return person;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (Objects.isNull(userDto)){throw new NotFoundException("userDto is null");}
        final String INSERT_SQL = "INSERT INTO ULAB_EDU.PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Saved user: {}", userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if (Objects.isNull(userDto) || Objects.isNull(userDto.getId())){
            throw new BadRequestExceptionUpdate(userDto);
        }
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        final String INSERT_SQL = "UPDATE ULAB_EDU.PERSON SET FULL_NAME=?, TITLE=?, AGE=? WHERE ID=?";
        int check = jdbcTemplate.update(INSERT_SQL,
                    user.getFullName(),
                    user.getTitle(),
                    user.getAge(),
                    user.getId());
        if (check==0){
            throw new NotFoundException("User not found");
        }
        log.info("updated user: {}", user);
        return userMapper.personToUserDto(user);
    }

    @Override
    public UserDto getUserById(Long id) {
        if (Objects.isNull(id)){throw new NotFoundException("id is null");}
        final String GET_SQL = "SELECT * FROM ULAB_EDU.PERSON WHERE ID=?";
        try {
            Person user = jdbcTemplate.queryForObject(
                    GET_SQL,
                    this::mapRowToPerson,
                    id);
            return userMapper.personToUserDto(user);
        } catch (EmptyResultDataAccessException e){
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public void deleteUserById(Long id) {
        if (Objects.isNull(id)){throw new NotFoundException("id is null");}
        final String DELETE_SQL = "DELETE FROM ULAB_EDU.PERSON WHERE ID=?";
        jdbcTemplate.update(DELETE_SQL, id);
    }
}
