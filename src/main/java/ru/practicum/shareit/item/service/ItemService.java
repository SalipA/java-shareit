package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto read(Long itemId);

    List<ItemDto> readAllByUserId(Long userId);

    List<ItemDto> searchItems(String text);
}