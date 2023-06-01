package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            item.getRequest());
    }

    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(itemDto.getRequestId());
        return item;
    }

    public List<ItemDto> listToItemDto(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }
}