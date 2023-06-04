package ru.practicum.shareit.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long bookingId) {
        super("Бронирование с bookingId = " + bookingId + " не найдено!");
    }
}