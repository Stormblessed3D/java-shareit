package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingDtoReceived;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingDateValidator implements ConstraintValidator<EndDateAfterStart, BookingDtoReceived> {
    @Override
    public void initialize(EndDateAfterStart constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDtoReceived bookingDtoReceived, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDtoReceived.getStart();
        LocalDateTime end = bookingDtoReceived.getEnd();
        if (start == null || end == null) {
            return false;
        }
        if (end.isEqual(start)) {
            return false;
        }
        return start.isBefore(end);
    }
}
