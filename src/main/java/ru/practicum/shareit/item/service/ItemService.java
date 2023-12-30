package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.CommentDtoRequest;
import ru.practicum.shareit.comment.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    List<ItemDtoResponse> getItems(Long ownerId);

    ItemDtoResponse getItemById(Long itemId, Long userId);

    ItemDtoResponse createItem(ItemDtoRequest itemDtoRequest, Long ownerId);

    ItemDtoResponse updateItem(ItemDtoRequest itemDtoRequest, Long itemId, Long ownerId);

    void deleteItem(Long itemId, Long ownerId);

    List<ItemDtoResponse> search(String text);

    CommentDtoResponse createComment(CommentDtoRequest commentDtoRequest, Long itemId, Long userId);
}
