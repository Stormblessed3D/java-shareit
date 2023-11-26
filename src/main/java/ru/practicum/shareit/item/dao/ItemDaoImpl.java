package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<Item> getItems(Long ownerId) {
        return items.values().stream()
                .filter(i -> i.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item createItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Item itemToUpdate = items.get(item.getId());
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemToUpdate;
    }

    @Override
    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }

    public List<Item> searchInName(String text) {
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase().trim()) && i.getAvailable())
                .collect(Collectors.toList());
    }

    public List<Item> searchInDescription(String text) {
        return items.values().stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase().trim()) && i.getAvailable())
                .collect(Collectors.toList());
    }

    private Long generateId() {
        return ++id;
    }
}
