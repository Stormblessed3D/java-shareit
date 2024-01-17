package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDtoToReturn toBookingDtoToReturn(Booking booking) {
        return BookingDtoToReturn.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(UserMapper.toUserForBookingDto(booking.getBooker()))
                .item(ItemMapper.toItemForBookingDto(booking.getItem()))
                .build();
    }

    public static List<BookingDtoToReturn> toBookingDtoToReturn(Iterable<Booking> bookings) {
        List<BookingDtoToReturn> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingDtoToReturn(booking));
        }
        return dtos;
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        return BookingForItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking toBooking(BookingDtoReceived bookingDtoReceived, Item item, User booker) {
        return Booking.builder()
                .start(bookingDtoReceived.getStart())
                .end(bookingDtoReceived.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }
}
