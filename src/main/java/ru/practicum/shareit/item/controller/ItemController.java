package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    public final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("POST: /items, userId = {}, value = {}", userId, itemDto);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH: /items/{}, userId = {}, value = {}", itemId, userId, itemDto);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto read(@PathVariable Long itemId) {
        log.info("GET: /items/{}", itemId);
        return itemService.read(itemId);
    }

    @GetMapping
    public List<ItemDto> readAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET: /items, userId = {}", userId);
        return itemService.readAllByUserId(userId);
    }

    @GetMapping(value = "/search", params = {"text"})
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET: /items/search, text = {}", text);
        return itemService.searchItems(text);
    }
}