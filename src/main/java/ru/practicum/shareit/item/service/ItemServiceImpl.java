package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<ItemDto> getItems(Long ownerId) {
        checkUser(ownerId);
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        return items.stream()
                .map(ItemMapper::toItemDtoWithBookings)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(cacheNames = "items", key = "#itemId")
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        if (checkIsOwner(item.getOwner().getId(), userId)) {
            return ItemMapper.toItemDtoWithBookings(item);
        } else {
            return ItemMapper.toItemDto(item);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", ownerId)));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, owner));
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CachePut(cacheNames = "items", key = "#itemId")
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        checkUser(ownerId);
        if (!Objects.equals(itemToUpdate.getOwner().getId(), ownerId)) {
            throw new EntityNotFoundException("Редактировать вещь может только владелец");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemToUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(itemToUpdate);
        return ItemMapper.toItemDto(itemToUpdate);
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
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByNameOrDescriptionContainingIgnoreCase(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        if (bookings.size() == 0) {
            throw new ConstraintViolationException("Комментарий может оставлять только пользователь, бронировавший вещь " +
                    "и только к завершенным бронированиям", null);
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, item, author));
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
