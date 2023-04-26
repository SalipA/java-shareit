package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.exception.InvalidEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class})
public class UserExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleValidationExp(final InvalidEmailException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundExp(final UserNotFoundException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleArgumentNotValidExp(final MethodArgumentNotValidException exp) {
        FieldError fieldError = exp.getBindingResult().getFieldError();
        if (fieldError != null) {
            String errorDefMess = fieldError.getDefaultMessage();
            log.error(errorDefMess);
            return Map.of("error", Objects.requireNonNullElse(errorDefMess, "Произошла ошибка валидации"));
        } else {
            return Map.of("error", "Произошла ошибка валидации");
        }
    }
}