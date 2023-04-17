package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User create(User user);

    User update(Long userId, User user);

    User read(Long userId);

    void delete(Long userId);

    List<User> readAll();

    void checkUserId(Long userId);

    void checkEmailDistinct(User user);
}