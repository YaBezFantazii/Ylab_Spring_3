package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BadRequestExceptionUpdate;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson  = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updatePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setId(1L);
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person updatedPerson  = new Person();
        updatedPerson.setId(1L);
        updatedPerson.setFullName("test name");
        updatedPerson.setAge(11);
        updatedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(userRepository.save(person)).thenReturn(updatedPerson);
        when(userMapper.personToUserDto(updatedPerson)).thenReturn(result);

        //then

        UserDto userDtoResult = userService.updateUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Получение пользователя. Должно пройти успешно.")
    void getPerson_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);
        person.setAge(11);
        person.setFullName("test name");
        person.setTitle("test title");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        //when
        when(userRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(userDto);
        //then
        assertEquals(1L, userService.getUserById(person.getId()).getId());
    }

    @Test
    @DisplayName("Удаление пользователя. Должно пройти успешно.")
    void deletePerson_Test() {
        // given
        Person person = new Person();
        person.setId(1L);
        // when
        when(userRepository.findById(person.getId())).thenReturn(Optional.of(person));
        userService.deleteUserById(person.getId());
        // then
        verify(userRepository).deleteById(person.getId());
    }

    @Test
    @DisplayName("Создание полльзователя c null. Должно выбросить ошибку")
    void savePersonWithNull_Test(){
        assertThatThrownBy(() -> userService.createUser(null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Обновление пользователя c null или id=null. Должно выбросить ошибку")
    void updatePersonWithNull_Test(){
        assertThatThrownBy(() -> userService.updateUser(null))
                .isInstanceOf(BadRequestExceptionUpdate.class);

        UserDto userDto = new UserDto();
        assertThatThrownBy(() -> userService.updateUser(userDto))
                .isInstanceOf(BadRequestExceptionUpdate.class);
    }

    @Test
    @DisplayName("Получение пользователя с id=null. Должно выбросить ошибку")
    void GetPersonWithNull_Test(){
        assertThatThrownBy(() -> userService.getUserById(null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Удаление пользователя с id=null. Должно выбросить ошибку")
    void DeletePersonWithNull_Test(){
        assertThatThrownBy(() -> userService.deleteUserById(null))
                .isInstanceOf(NotFoundException.class);
    }

}
