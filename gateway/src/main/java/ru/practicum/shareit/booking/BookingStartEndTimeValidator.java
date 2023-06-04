package ru.practicum.shareit.booking;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingStartEndTimeValidator implements ConstraintValidator<BookingStartEndTimeConstraint, BookingDto> {
    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        return !bookingDto.getStart().equals(bookingDto.getEnd()) &&
            !bookingDto.getStart().isAfter(bookingDto.getEnd());
    }
}