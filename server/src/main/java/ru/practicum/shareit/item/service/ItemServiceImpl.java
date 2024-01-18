package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDtoRequest;
import ru.practicum.shareit.comment.CommentDtoResponse;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<ItemDtoResponse> getItems(Long ownerId, Integer from, Integer size) {
        checkUser(ownerId);
        int page = from / size;
        List<Item> items = itemRepository.findByOwnerId(ownerId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        Map<Item, List<Booking>> bookings = bookingRepository.findByItemInAndStatus(items, BookingStatus.APPROVED,
                        Sort.by(ASC, "start")).stream()
                .collect(groupingBy(Booking::getItem));
        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(ASC, "created")).stream()
                .collect(groupingBy(Comment::getItem));
        List<ItemDtoResponse> itemsWithBookingsAndComments = new ArrayList<>();
        for (Item item : items) {
            ItemDtoResponse itemDto = ItemMapper.toItemDtoWithBookings(item,
                    comments.getOrDefault(item, Collections.emptyList()),
                    bookings.getOrDefault(item, Collections.emptyList()));
            itemsWithBookingsAndComments.add(itemDto);
        }
        return itemsWithBookingsAndComments;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(cacheNames = "items", key = "#itemId")
    public ItemDtoResponse getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        List<Booking> bookings = bookingRepository.findByItemAndStatus(item, BookingStatus.APPROVED, Sort.by(ASC, "start"));
        List<Comment> comments = commentRepository.findByItem(item);
        if (checkIsOwner(item.getOwner().getId(), userId)) {
            return ItemMapper.toItemDtoWithBookings(item, comments, bookings);
        } else {
            return ItemMapper.toItemDtoWithoutBookings(item, comments);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ItemDtoResponse createItem(ItemDtoRequest itemDtoRequest, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", ownerId)));
        if (itemDtoRequest.getRequestId() != null) {
            Request request = requestRepository.findById(itemDtoRequest.getRequestId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Запрос с id %d не найден", itemDtoRequest.getRequestId())));
            Item item = itemRepository.save(ItemMapper.toItem(itemDtoRequest, owner, request));
            return ItemMapper.toItemDtoWithoutBookings(item, List.of());
        } else {
            Item item = itemRepository.save(ItemMapper.toItem(itemDtoRequest, owner, null));
            return ItemMapper.toItemDtoWithoutBookings(item, List.of());
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CachePut(cacheNames = "items", key = "#itemId")
    public ItemDtoResponse updateItem(ItemDtoRequest itemDtoRequest, Long itemId, Long ownerId) {
        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        checkUser(ownerId);
        if (!Objects.equals(itemToUpdate.getOwner().getId(), ownerId)) {
            throw new EntityNotFoundException("Редактировать вещь может только владелец");
        }
        if (itemDtoRequest.getName() != null && !itemDtoRequest.getName().isBlank()) {
            itemToUpdate.setName(itemDtoRequest.getName());
        }
        if (itemDtoRequest.getDescription() != null && !itemDtoRequest.getDescription().isBlank()) {
            itemToUpdate.setDescription(itemDtoRequest.getDescription());
        }
        if (itemDtoRequest.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDtoRequest.getAvailable());
        }
        Item item = itemRepository.save(itemToUpdate);
        List<Comment> comments = commentRepository.findByItem(item);
        return ItemMapper.toItemDtoWithoutBookings(item, comments);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(cacheNames = "items", key = "#itemId")
    public void deleteItem(Long itemId, Long ownerId) {
        Item itemToDelete = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        if (!Objects.equals(itemToDelete.getOwner().getId(), ownerId)) {
            throw new EntityNotFoundException("Удалить вещь может только владелец");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<ItemDtoResponse> search(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return List.of();
        }
        int page = from / size;
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCase(text,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        Map<Item, List<Booking>> bookings = bookingRepository.findByItemInAndStatus(items, BookingStatus.APPROVED,
                        Sort.by(ASC, "start")).stream()
                .collect(groupingBy(Booking::getItem));
        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created")).stream()
                .collect(groupingBy(Comment::getItem));
        List<ItemDtoResponse> itemsWithComments = new ArrayList<>();
        for (Item item : items) {
            ItemDtoResponse itemDto = ItemMapper.toItemDtoWithBookings(item,
                    comments.getOrDefault(item, Collections.emptyList()),
                    bookings.getOrDefault(item, Collections.emptyList()));
            itemsWithComments.add(itemDto);
        }
        return itemsWithComments;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDtoResponse createComment(CommentDtoRequest commentDtoRequest, Long itemId, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        Long count = bookingRepository.countByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        if (count == 0) {
            throw new UnavailableItemException("Комментарий может оставлять только пользователь, бронировавший вещь " +
                    "и только к завершенным бронированиям");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDtoRequest, item, author));
        return CommentMapper.toCommentDto(comment);
    }

    private void checkUser(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", ownerId));
        }
    }

    private boolean checkIsOwner(Long ownerId, Long userId) {
        return Objects.equals(ownerId, userId);
    }
}
