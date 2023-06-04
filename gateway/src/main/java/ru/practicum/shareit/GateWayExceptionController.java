package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, ItemController.class, BookingController.class, ItemRequestController.class})
public class GateWayExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleStartEndValidExp(final ConstraintViolationException exp) {
        log.error(exp.getMessage());
        ConstraintViolation<?> violation = exp.getConstraintViolations().iterator().next();
        return Map.of("error", violation.getMessageTemplate());
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlePaginationParamExp(final PaginationParamException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBookingUnknownState(final MethodArgumentTypeMismatchException exp) {
        log.error(exp.getMessage());
        return Map.of("error", "Unknown state: " + exp.getValue());
    }
}