package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDtoResponse toItemDtoWithoutBookings(Item item, List<Comment> comments) {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (comments != null) {
            itemDtoResponse.setComments(CommentMapper.toCommentDto(comments));
        } else {
            itemDtoResponse.setComments(List.of());
        }
        return itemDtoResponse;
    }

    public static ItemDtoResponse toItemDtoWithBookings(Item item, List<Comment> comments, List<Booking> bookings) {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (bookings != null && (bookings.size() != 0)) {
            List<Booking> nextBookings = bookings.stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getStart))
                    .collect(Collectors.toList());
            if (nextBookings.size() != 0) {
                BookingForItemDto nextBooking = new BookingForItemDto();
                nextBooking.setId(nextBookings.get(0).getId());
                nextBooking.setBookerId(nextBookings.get(0).getBooker().getId());
                itemDtoResponse.setNextBooking(nextBooking);
            }

            List<Booking> lastBookings = bookings.stream()
                    .filter(b -> !b.getStart().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Booking::getEnd).reversed())
                    .collect(Collectors.toList());
            BookingForItemDto lastBooking = new BookingForItemDto();
            if (lastBookings.size() != 0) {
                lastBooking.setId(lastBookings.get(0).getId());
                lastBooking.setBookerId(lastBookings.get(0).getBooker().getId());
                itemDtoResponse.setLastBooking(lastBooking);
            }
        }
        if (comments != null) {
            itemDtoResponse.setComments(CommentMapper.toCommentDto(comments));
        } else {
            itemDtoResponse.setComments(List.of());
        }
        return itemDtoResponse;
    }

    public static ItemForBookingDto toItemForBookingDto(Item item) {
        return ItemForBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static Item toItem(ItemDtoRequest itemDtoRequest, User owner) {
        return Item.builder()
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .owner(owner)
                .build();
    }
}
