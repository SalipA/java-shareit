package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    public ItemDto read(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("GET: /items/{}, userId = {}", itemId, userId);
        return itemService.read(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> readAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required =
        false) @Nullable @Min(0) Integer from, @RequestParam(required = false) @Nullable @Min(1) Integer size) {
        log.info("GET: /items, userId = {}, pagination: from {}, size {}",userId, from, size);
        return itemService.readAllByUserId(userId, from, size);
    }

    @GetMapping(value = "/search", params = {"text"})
    public List<ItemDto> searchItems(@RequestParam String text, @RequestParam(required =
        false) @Nullable @Min(0) Integer from, @RequestParam(required = false) @Nullable @Min(1) Integer size) {
        log.info("GET: /items/search, text = {}, pagination: from {}, size {}", text, from, size);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                    @RequestBody @Valid CommentDto comment) {
        log.info("POST: /items/{}/comment, value = {}", itemId, comment);
        return itemService.createComment(userId, itemId, comment);
    }
}