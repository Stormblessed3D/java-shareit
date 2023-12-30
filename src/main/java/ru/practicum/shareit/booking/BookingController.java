package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.model.BookingStatusState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<BookingDtoToReturn> createBooking(@Valid @RequestBody BookingDtoReceived bookingDtoReceived,
                                                            @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(bookingService.createBooking(bookingDtoReceived, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoToReturn> approveBooking(@PathVariable @Positive Long bookingId,
                                                     @RequestParam(value = "approved") Boolean isApproved,
                                                     @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(bookingService.approveBooking(bookingId, isApproved, userId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoToReturn> getBookingById(@PathVariable @Positive Long bookingId,
                                                     @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoToReturn>> getAllBookingsByUser(@RequestParam(defaultValue = "ALL") BookingStatusState state,
                                                                 @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(bookingService.getAllBookingsByUser(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoToReturn>> getAllBookingsByOwner(@RequestParam(defaultValue = "ALL") BookingStatusState state,
                                                                  @RequestHeader(USER_REQUEST_HEADER) Long ownerId) {
        return ResponseEntity.ok(bookingService.getAllBookingsByOwner(ownerId, state));
    }
}
