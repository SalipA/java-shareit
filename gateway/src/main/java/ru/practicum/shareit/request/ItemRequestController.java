package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(value = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST: /requests, userId = {}, value = {}", userId, itemRequestDto);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> read(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("GET: /requests/{}, userId = {}", requestId, userId);
        return itemRequestClient.read(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET: /requests, userId = {}", userId);
        return itemRequestClient.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsWithPagination(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(required = false) @Nullable @Min(0) Integer from,
                                                               @RequestParam(required = false) @Nullable @Min(1) Integer size) {
        log.info("GET: /requests/all, userId = {}, pagination: from {} size {}", userId, from, size);
        return itemRequestClient.getAllRequestsWithPagination(userId, from, size);
    }
}