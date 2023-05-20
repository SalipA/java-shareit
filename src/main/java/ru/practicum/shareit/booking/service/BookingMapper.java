package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.user.dto.UserView;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {
    public Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(BookingStatuses.WAITING);
        return booking;
    }

    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(new ItemView(booking.getItem().getId(), booking.getItem().getName()));
        bookingDto.setBooker(new UserView(booking.getBooker().getId()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public List<BookingDto> listToBookingDto(List<Booking> bookings) {
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsDto.add(toBookingDto(booking));
        }
        return bookingsDto;
    }
}