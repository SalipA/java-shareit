package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated());
        itemRequestDto.setItems(itemRequest.getItems());
        return itemRequestDto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public List<ItemRequestDto> listToItemRequestDto(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemsRequestsDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemsRequestsDto.add(toItemRequestDto(itemRequest));
        }
        return itemsRequestsDto;
    }
}
