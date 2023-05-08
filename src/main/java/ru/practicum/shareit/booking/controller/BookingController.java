package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.annotation.BookingStartEndTimeConstraint;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.States;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody @BookingStartEndTimeConstraint BookingDto bookingDto) {
        log.info("POST: /bookings, userId = {}, value = {}", userId, bookingDto);
        return bookingService.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto read(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("GET: /bookings/{}, userId = {}", bookingId, userId);
        return bookingService.read(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                     @RequestParam @NotNull Boolean approved) {
        log.info("PATCH: /bookings/{}, userId = {}", bookingId, userId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingDto> getAllByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(required = false) States state) {
        if (state == null) {
            state = States.ALL;
        }
        log.info("GET: /bookings, userId = {}, state = {}", userId, state);
        return bookingService.getAllByState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwnerAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false) States state) {
        if (state == null) {
            state = States.ALL;
        }
        log.info("GET: /bookings/owner, userId = {}, state = {}", userId, state);
        return bookingService.getAllByOwnerAndState(userId, state);
    }
}