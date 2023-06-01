package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingOwnerCreateException;
import ru.practicum.shareit.booking.exception.BookingReadAccessException;
import ru.practicum.shareit.booking.exception.BookingStatusAlreadyChangedException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.exception.CommentAddAccessException;
import ru.practicum.shareit.item.exception.ItemBookingAccessException;
import ru.practicum.shareit.item.exception.ItemEditAccessException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.exception.InvalidEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice(assignableTypes = {ItemController.class, BookingController.class, UserController.class,
    ItemRequestController.class})

public class ShareItAppExceptionController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlePaginationParamExp(final PaginationParamException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleRequestNotFoundExp(final RequestNotFoundException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFoundExp(final UserNotFoundException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUserValidationExp(final InvalidEmailException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleItemEditAccessExp(final ItemEditAccessException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingHeaderExp(final MissingRequestHeaderException exp) {
        log.error(exp.getMessage());
        return Map.of("error", "userId владельца вещи должен быть указан");
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
    public Map<String, String> handleItemBookingAccess(final ItemBookingAccessException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBookingStatusAlready(final BookingStatusAlreadyChangedException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookingUserAccess(final BookingReadAccessException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookingOwnerCreate(final BookingOwnerCreateException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookingNotFoundExp(final BookingNotFoundException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBookingUnknownState(final MethodArgumentTypeMismatchException exp) {
        log.error(exp.getMessage());
        return Map.of("error", "Unknown state: " + exp.getValue());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleStartEndValidExp(final ConstraintViolationException exp) {
        log.error(exp.getMessage());
        ConstraintViolation<?> violation = exp.getConstraintViolations().iterator().next();
        return Map.of("error", violation.getMessageTemplate());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCommentAddAccessExp(final CommentAddAccessException exp) {
        return Map.of("error", exp.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleItemNotFoundExp(final ItemNotFoundException exp) {
        return Map.of("error", exp.getMessage());
    }
}
