package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    List<Item> getItems(Long ownerId);
    Optional<Item> getItemById(Long itemId);
    Item createItem(Item item);
    Item updateItem(Item item);
    void deleteItem(Long itemId);
    List<Item> searchInName(String text);
    List<Item> searchInDescription(String text);
}
