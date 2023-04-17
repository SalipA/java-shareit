package ru.practicum.shareit.item.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long itemId) {
        super("Вещь с userId = " + itemId + " не найдена!");
    }
}