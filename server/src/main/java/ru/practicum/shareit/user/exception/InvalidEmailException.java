package ru.practicum.shareit.user.exception;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String userEmail) {
        super("Email: " + userEmail + " уже используется");
    }
}