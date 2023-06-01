package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long itemId) {
        super("Вещь с itemId = " + itemId + " не найдена!");
    }

    public ItemNotFoundException(List<Item> items, Long userId) {
        super("Пользовател userId = " + userId + " не добавил ни одну вещь для аренды");
    }
}