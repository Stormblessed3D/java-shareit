package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getComments() != null) {
            itemDto.setComments(CommentMapper.toCommentDto(new ArrayList<>(item.getComments())));
        } else {
            itemDto.setComments(List.of());
        }
        return itemDto;
    }

    public static ItemDto toItemDtoWithBookings(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getBookings() != null && (item.getBookings().size() != 0)) {
            List<Booking> bookings = new ArrayList<>(item.getBookings());
            List<Booking> nextBookings = bookings.stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()) && Objects.equals(b.getStatus(), BookingStatus.APPROVED))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .collect(Collectors.toList());
            if (nextBookings.size() != 0) {
                BookingForItemDto nextBooking = new BookingForItemDto();
                nextBooking.setId(nextBookings.get(0).getId());
                nextBooking.setBookerId(nextBookings.get(0).getBooker().getId());
                itemDto.setNextBooking(nextBooking);
            }

            List<Booking> lastBookings = bookings.stream()
                    .filter(b -> ((b.getEnd().isBefore(LocalDateTime.now()))
                            || (b.getStart().isBefore(LocalDateTime.now()) && b.getEnd().isAfter(LocalDateTime.now())))
                            && Objects.equals(b.getStatus(), BookingStatus.APPROVED))
                    .sorted(Comparator.comparing(Booking::getEnd).reversed())
                    .collect(Collectors.toList());
            BookingForItemDto lastBooking = new BookingForItemDto();
            if (lastBookings.size() != 0) {
                lastBooking.setId(lastBookings.get(0).getId());
                lastBooking.setBookerId(lastBookings.get(0).getBooker().getId());
                itemDto.setLastBooking(lastBooking);
            }
        }
        if (item.getComments() != null) {
            itemDto.setComments(CommentMapper.toCommentDto(new ArrayList<>(item.getComments())));
        } else {
            itemDto.setComments(List.of());
        }
        return itemDto;
    }

    public static ItemForBookingDto toItemForBookingDto(Item item) {
        return ItemForBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }
}
