package ru.practicum.shareit.booking;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = BookingStartEndTimeValidator.class)
public @interface BookingStartEndTimeConstraint {
    String message() default "Дата окончания бронирования должна быть позднее даты начала бронирования";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
