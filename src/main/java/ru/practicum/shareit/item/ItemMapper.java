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
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDtoResponse toItemDtoWithoutBookings(Item item, List<Comment> comments) {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        itemDtoResponse.setComments(CommentMapper.toCommentDto(comments));
        return itemDtoResponse;
    }

    public static ItemDtoResponse toItemDtoWithBookings(Item item, List<Comment> comments, List<Booking> bookings) {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (bookings.size() != 0) {
            Optional<Booking> nextBooking = bookings.stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .findFirst();
            if (nextBooking.isPresent()) {
                BookingForItemDto nextBookingDto = new BookingForItemDto();
                nextBookingDto.setId(nextBooking.get().getId());
                nextBookingDto.setBookerId(nextBooking.get().getBooker().getId());
                itemDtoResponse.setNextBooking(nextBookingDto);
            }

            Optional<Booking> lastBooking = bookings.stream()
                    .filter(b -> !b.getStart().isAfter(LocalDateTime.now()))
                    .reduce((first, second) -> second);
            if (lastBooking.isPresent()) {
                BookingForItemDto lastBookingDto = new BookingForItemDto();
                lastBookingDto.setId(lastBooking.get().getId());
                lastBookingDto.setBookerId(lastBooking.get().getBooker().getId());
                itemDtoResponse.setLastBooking(lastBookingDto);
            }
        }
        itemDtoResponse.setComments(CommentMapper.toCommentDto(comments));
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
