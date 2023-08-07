package ru.practicum.shareit.request;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Заявки", description = "Операции для работы с предзаказами на вещи")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @Operation(
        summary = "Создания предзаказа на вещь",
        description = "Позволяет пользователю оставлять предзаказ на вещь, которую еще никто не добавил"
    )
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST: /requests, userId = {}, value = {}", userId, itemRequestDto);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @Operation(
        summary = "Получение информации о предзаказе на вещь",
        description = "Позволяет пользователям получать информацию о предзаказе на вещь"
    )
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> read(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("GET: /requests/{}, userId = {}", requestId, userId);
        return itemRequestClient.read(userId, requestId);
    }

    @Operation(
        summary = "Получение списка предзаказов пользователя",
        description = "Позволяет пользователю получать список своих предзаказе на вещи"
    )
    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET: /requests, userId = {}", userId);
        return itemRequestClient.getAllByUserId(userId);
    }

    @Operation(
        summary = "Получение списка всех предзаказов",
        description = "Позволяет пользователю получать список предзаказов на вещи, созданных другими пользователями"
    )
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsWithPagination(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(required = false) @Nullable @Min(0) Integer from,
                                                               @RequestParam(required = false) @Nullable @Min(1) Integer size) {
        log.info("GET: /requests/all, userId = {}, pagination: from {} size {}", userId, from, size);
        return itemRequestClient.getAllRequestsWithPagination(userId, from, size);
    }
}