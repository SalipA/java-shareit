package ru.practicum.shareit.booking.exception;

public class BookingStatusAlreadyChangedException extends RuntimeException {
    public BookingStatusAlreadyChangedException(Long bookingId) {
        super("Статус бронирования bookingId = " + bookingId + " уже был изменен");
    }
}