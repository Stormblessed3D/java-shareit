package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.StatusException;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.constant.ConstantKeeper.USER_REQUEST_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDtoReceived bookingDtoReceived,
                                                @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return bookingClient.bookItem(userId, bookingDtoReceived);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable @Positive Long bookingId,
                                                 @RequestParam(value = "approved") Boolean isApproved,
                                                 @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return bookingClient.approveBooking(bookingId, isApproved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable @Positive Long bookingId,
                                                 @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUser(
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") @Min(value = 0L) Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive @Max(value = 100) Integer size,
            @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new StatusException("Unknown state: " + stateParam));
        return bookingClient.getBookings(userId, state, from, size, false);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") @Min(value = 0L) Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive @Max(value = 100) Integer size,
            @RequestHeader(USER_REQUEST_HEADER) Long ownerId) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new StatusException("Unknown state: " + stateParam));
        return bookingClient.getBookings(ownerId, state, from, size, true);
    }
}

