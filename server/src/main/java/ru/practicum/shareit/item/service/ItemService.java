package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto read(Long itemId, Long userId);

    List<ItemDto> readAllByUserId(Long userId, Integer from, Integer size);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    CommentDto createComment(Long userId, Long itemId, CommentDto comment);

    Item checkItem(Long itemId);

    List<Item> findItemsByOwner(Long userId);

    Item checkItemIsAvailableForBooking(Long userId, Long itemId);
}