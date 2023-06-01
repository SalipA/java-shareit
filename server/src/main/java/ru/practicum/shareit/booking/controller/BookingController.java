package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.States;

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
                             @RequestBody BookingDto bookingDto) {
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
                                     @RequestParam String approved) {
        log.info("PATCH: /bookings/{}, userId = {}", bookingId, userId);
        return bookingService.approveBooking(userId, bookingId, Boolean.parseBoolean(approved));
    }

    @GetMapping
    public List<BookingDto> getAllByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(required = false, name = "state") String stringState,
                                          @RequestParam(required =
        false) Integer from, @RequestParam(required = false)  Integer size) {
        States state = States.ALL;
        if (stringState != null) {
            state = States.stringToState(stringState);
        }
        log.info("GET: /bookings, userId = {}, state = {}, pagination: from {} size {}", userId, state, from,
            size);
        return bookingService.getAllByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwnerAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false, name = "state") String stringState,
                                                  @RequestParam(required =
        false)  Integer from, @RequestParam(required = false)  Integer size) {
        States state = States.ALL;
        if (stringState != null) {
           state = States.stringToState(stringState);
        }
        log.info("GET: /bookings/owner, userId = {}, state = {}, pagination: from {} size {}", userId, state, from,
            size);
        return bookingService.getAllByOwnerAndState(userId, state, from, size);
    }
}