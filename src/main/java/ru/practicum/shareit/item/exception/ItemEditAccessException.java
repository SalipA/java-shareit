package ru.practicum.shareit.item.exception;

public class ItemEditAccessException extends RuntimeException {
    public ItemEditAccessException(Long userId, Long itemId) {
        super("Ошибка редактирования: пользователь с userId = " + userId + " не является владельцем вещи с itemId =" +
            itemId);
    }
}