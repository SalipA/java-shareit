package ru.practicum.shareit.booking.exception;

public class BookingStartEndTimeValidationException extends RuntimeException {
    public BookingStartEndTimeValidationException(String message) {
        super(message);
    }
}