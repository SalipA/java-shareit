package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    UserDto read(Long userId);

    void delete(Long userId);

    List<UserDto> readAll();

    User checkUser(Long userId);
}