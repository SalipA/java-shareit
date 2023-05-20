package ru.practicum.shareit.item.exception;

public class ItemBookingAccessException extends RuntimeException {
    public ItemBookingAccessException(Long itemId) {
        super("Ошибка бронирования: вещь с itemId = " + itemId + " не доступна для бронирования");
    }
}
