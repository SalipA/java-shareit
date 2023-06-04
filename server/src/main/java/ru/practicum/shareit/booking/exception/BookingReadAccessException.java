package ru.practicum.shareit.booking.exception;

public class BookingReadAccessException extends RuntimeException {
    public BookingReadAccessException(Long userId, Long bookingId) {
        super("У пользователя userId = " + userId + " нет прав для просмотра информации о бронировании bookingId = "
            + bookingId);
    }
}