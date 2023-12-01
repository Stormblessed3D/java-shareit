package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long ownerId);

    Item getItemById(Long itemId);

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Long itemId, Long ownerId);

    List<Item> search(String text);
}
