package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto read(Long userId, Long requestId);

    List<ItemRequestDto> getAllByUserId(Long userId);

    List<ItemRequestDto> getAllRequestsWithPagination(Long userId, Integer from, Integer size);
}
