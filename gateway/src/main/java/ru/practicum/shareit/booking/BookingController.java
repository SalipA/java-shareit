package ru.practicum.shareit.booking;

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
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody @BookingStartEndTimeConstraint BookingDto bookingDto) {
        log.info("POST: /bookings, userId = {}, value = {}", userId, bookingDto);
        return bookingClient.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> read(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                       @PathVariable Long bookingId) {
        log.info("GET: /bookings/{}, userId = {}", bookingId, userId);
        return bookingClient.read(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam @NotNull Boolean approved) {
        log.info("PATCH: /bookings/{}, userId = {}", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, Boolean.toString(approved));
    }

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