package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(value = "/items")
@Slf4j
public class ItemController {

    public final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody ItemDto itemDto) {
        log.info("POST: /items, userId = {}, value = {}", userId, itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("PATCH: /items/{}, userId = {}, value = {}", itemId, userId, itemDto);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> read(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("GET: /items/{}, userId = {}", itemId, userId);
        return itemClient.read(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> readAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(required =
        false) @Nullable @Min(0) Integer from, @RequestParam(required = false) @Nullable @Min(1) Integer size) {
        log.info("GET: /items, userId = {}, pagination: from {}, size {}", userId, from, size);
        return itemClient.readAllByUserId(userId, from, size);
    }

    @GetMapping(value = "/search", params = {"text"})
    public ResponseEntity<Object> searchItems(@RequestParam String text, @RequestParam(required =
        false) @Nullable @Min(0) Integer from, @RequestParam(required = false) @Nullable @Min(1) Integer size) {
        log.info("GET: /items/search, text = {}, pagination: from {}, size {}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                    @RequestBody @Valid CommentDto comment) {
        log.info("POST: /items/{}/comment, value = {}", itemId, comment);
        return itemClient.createComment(userId, itemId, comment);
    }
}
