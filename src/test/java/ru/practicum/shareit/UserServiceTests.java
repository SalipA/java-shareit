package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dao.UserDaoImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

public class UserServiceTests {
    UserService userService;
    UserDto standardCaseUserDto;

    @BeforeEach
    public void createUserService() {
        userService = new UserServiceImpl(new UserDaoImpl(), new UserMapper());
    }

    @BeforeEach
    public void createStandardUserDto() {
        standardCaseUserDto = new UserDto(0L, "testName", "test@test.ru");
    }

    @Test
    public void shouldCreateUserStandardCase() {
        UserDto testUserDto = userService.create(standardCaseUserDto);
        Assertions.assertEquals(1L, testUserDto.getId());
        Assertions.assertEquals(userService.read(1L), testUserDto);
        Assertions.assertEquals(1, userService.readAll().size());
    }

    @Test
    public void shouldUpdateUserStandardCase() {
        UserDto newUser = new UserDto(1L, "testNameUpdate", "testUpdate@test.ru");
        userService.create(standardCaseUserDto);
        UserDto updatedUser = userService.update(1L, newUser);
        Assertions.assertEquals(newUser, updatedUser);
        Assertions.assertEquals(userService.read(1L), newUser);
        Assertions.assertEquals(1, userService.readAll().size());
    }

    @Test
    public void shouldReadUserStandardCase() {
        UserDto userDto = userService.create(standardCaseUserDto);
        standardCaseUserDto.setId(1L);
        Assertions.assertEquals(userDto, standardCaseUserDto);
    }

    @Test
    public void shouldDeleteUserStandardCase() {
        userService.create(standardCaseUserDto);
        userService.delete(1L);
        Assertions.assertEquals(0, userService.readAll().size());
        final UserNotFoundException exp = Assertions.assertThrows(UserNotFoundException.class,
            () -> userService.read(1L)
        );
        Assertions.assertEquals("Пользователь с userId = 1 не найден!",
            exp.getMessage());
    }

    @Test
    public void shouldReadAllUsers() {
        userService.create(standardCaseUserDto);
        userService.create(new UserDto(2L, "testNameUpdate", "testUpdate@test.ru"));
        Assertions.assertEquals(2, userService.readAll().size());
        userService.delete(1L);
        Assertions.assertEquals(1, userService.readAll().size());
    }
}