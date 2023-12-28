package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long ownerId);

    ItemDto getItemById(Long itemId, Long userId);

    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    void deleteItem(Long itemId, Long ownerId);

    List<ItemDto> search(String text);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}
