package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto read(Long userId, Long bookingId);

    BookingDto approveBooking(Long userId, Long bookingId, Boolean approved);

    List<BookingDto> getAllByState(Long userId, States state);

    List<BookingDto> getAllByOwnerAndState(Long userId, States state);

    Optional<Booking> findNextBookingForItem(Long itemId, BookingStatuses status, LocalDateTime dateTime);

    Optional<Booking> findLastBookingForItem(Long itemId, BookingStatuses status, LocalDateTime dateTime);

    List<Booking> findAllNextBookingForItems(List<Item> items, BookingStatuses status, LocalDateTime dateTime);

    List<Booking> findAllLastBookingForItems(List<Item> items, BookingStatuses status, LocalDateTime dateTime);

    Optional<Booking> findEndedBookingForItemByUser(Long userId, Long itemId, LocalDateTime dateTime);
}
