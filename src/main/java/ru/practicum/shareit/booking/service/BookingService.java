package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto read(Long userId, Long bookingId);

    BookingDto approveBooking(Long userId, Long bookingId, Boolean approved);

    List<BookingDto> getAllByState(Long userId, States state);

    List<BookingDto> getAllByOwnerAndState(Long userId, States state);
}
