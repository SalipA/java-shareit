package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item create(Item item);

    Item update(Long itemId, Item item);

    Item read(Long itemId);

    List<Item> readAllByUserId(Long userId);

    List<Item> searchItems(String text);

    void checkItemId(Long itemId);
}
