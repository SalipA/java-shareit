package ru.practicum.shareit.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Бронирования", description = "Операции для работы с заявками на бронь вещи")
public class BookingController {
    private final BookingClient bookingClient;
    @Operation(
        summary = "Создание заявки на бронирование вещи",
        description = "Позволяет пользователю создать заявку на бронирование вещи"
    )
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody @BookingStartEndTimeConstraint BookingDto bookingDto) {
        log.info("POST: /bookings, userId = {}, value = {}", userId, bookingDto);
        return bookingClient.create(userId, bookingDto);
    }
    @Operation(
        summary = "Получение информации о бронировании",
        description = "Позволяет пользователю, создавшему заявку на бронирование, и владельцу вещи получить " +
            "информацию о бронировании "
    )
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> read(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId) {
        log.info("GET: /bookings/{}, userId = {}", bookingId, userId);
        return bookingClient.read(userId, bookingId);
    }

    @Operation(
        summary = "Изменения статуса бронирования владельцем вещи",
        description = "Позволяет владельцу одобрять или отклонять пользовательские запросы на бронирование вещей"
    )
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam @NotNull Boolean approved) {
        log.info("PATCH: /bookings/{}, userId = {}", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, Boolean.toString(approved));
    }
    @Operation(
        summary = "Получение списка бронирований пользователем",
        description = "Позволяет пользователю получить список своих бронирований"
    )
    @GetMapping
    public ResponseEntity<Object> getAllByState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                @RequestParam(required = false) States state, @RequestParam(required =
        false) @Nullable @Min(0) Integer from, @RequestParam(required = false) @Nullable @Min(1) Integer size) {
        if (state == null) {
            state = States.ALL;
        }
        log.info("GET: /bookings, userId = {}, state = {}, pagination: from {} size {}", userId, state, from,
            size);
        return bookingClient.getAllByState(userId, state.toString(), from, size);
    }
    @Operation(
        summary = "Получение списка бронирований владельцем вещи",
        description = "Позволяет владельцу вещи получить список ее бронирований"
    )
    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerAndState(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                        @RequestParam(required = false) States state, @RequestParam(required =
        false) @Nullable @Min(0) Integer from, @RequestParam(required = false) @Nullable @Min(1) Integer size) {
        if (state == null) {
            state = States.ALL;
        }
        log.info("GET: /bookings/owner, userId = {}, state = {}, pagination: from {} size {}", userId, state, from,
            size);
        return bookingClient.getAllByOwnerAndState(userId, state.toString(), from, size);
    }
}