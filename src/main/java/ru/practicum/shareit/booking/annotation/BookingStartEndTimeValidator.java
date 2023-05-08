package ru.practicum.shareit.booking.annotation;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingStartEndTimeValidator implements ConstraintValidator<BookingStartEndTimeConstraint, BookingDto> {
    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        return !bookingDto.getStart().equals(bookingDto.getEnd()) &&
            !bookingDto.getStart().isAfter(bookingDto.getEnd());
    }
}