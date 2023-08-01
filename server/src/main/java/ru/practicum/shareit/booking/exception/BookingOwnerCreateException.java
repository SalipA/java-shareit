package ru.practicum.shareit.booking.exception;

public class BookingOwnerCreateException extends RuntimeException {
    public BookingOwnerCreateException(Long userId, Long itemId) {
        super("Бронирование вещи itemId = " + itemId + " создать не возможно. Пользователь userId = " + userId +
            " является ее владельцем");
    }
}