package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.model.BookingStatusState;

import java.util.List;

public interface BookingService {
    BookingDtoToReturn createBooking(BookingDtoReceived bookingDtoReceived, Long userId);

    BookingDtoToReturn approveBooking(Long bookingId, Boolean isApproved, Long userId);

    BookingDtoToReturn getBookingById(Long bookingId, Long userId);

    List<BookingDtoToReturn> getAllBookingsByUser(Long userId, BookingStatusState state, Integer from, Integer size);

    List<BookingDtoToReturn> getAllBookingsByOwner(Long ownerId, BookingStatusState state, Integer from, Integer size);
}
