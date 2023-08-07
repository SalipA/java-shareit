package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST: /requests, userId = {}, value = {}", userId, itemRequestDto);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto read(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("GET: /requests/{}, userId = {}", requestId, userId);
        return itemRequestService.read(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET: /requests, userId = {}", userId);
        return itemRequestService.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequestsWithPagination(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam(required = false)  Integer from,
                                                             @RequestParam(required = false)  Integer size) {
        log.info("GET: /requests/all, userId = {}, pagination: from {} size {}", userId, from, size);
        return itemRequestService.getAllRequestsWithPagination(userId, from, size);
    }
}