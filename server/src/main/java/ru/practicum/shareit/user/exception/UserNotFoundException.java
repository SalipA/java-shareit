package ru.practicum.shareit.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("Пользователь с userId = " + userId + " не найден!");
    }
}