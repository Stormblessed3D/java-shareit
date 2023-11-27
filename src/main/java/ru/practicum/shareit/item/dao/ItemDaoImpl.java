package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private final  Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();
    private Long id = 0L;

    @Override
    public List<Item> getItems(Long ownerId) {
        return userItemIndex.get(ownerId);
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item createItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        final List<Item> items = userItemIndex.computeIfAbsent(item.getOwnerId(), k -> new ArrayList<>());
        items.add(item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Item itemToUpdate = items.get(item.getId());
        if (item.getName() != null && !item.getName().isBlank()) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null &&!item.getDescription().isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        List<Item> itemsByOwner = userItemIndex.get(item.getOwnerId());
        itemsByOwner.removeIf(i -> i.getId() == item.getId());
        itemsByOwner.add(itemToUpdate);
        return itemToUpdate;
    }

    @Override
    public void deleteItem(Long itemId) {
        items.remove(itemId);
        Item item = getItemById(itemId).get();
        List<Item> itemsByOwner = userItemIndex.get(item.getOwnerId());
        itemsByOwner.removeIf(i -> i.getId() == item.getId());
    }

    public List<Item> searchByNameAndDescription(String text) {
        String lowerCaseText = text.toLowerCase().trim();
        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(lowerCaseText)
                        || i.getDescription().toLowerCase().contains(lowerCaseText)) && i.getAvailable())
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    private Long generateId() {
        return ++id;
    }
}
