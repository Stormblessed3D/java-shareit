package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public List<Item> getItems(Long ownerId) {
        checkUser(ownerId);
        return itemDao.getItems(ownerId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemDao.getItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с id %d не найдена", itemId)));
    }

    @Override
    public Item createItem(Item item) {
        checkUser(item.getOwnerId());
        Item createdItem = itemDao.createItem(item);
        log.info("Новая вещь c id {} создана", createdItem.getId());
        return createdItem;
    }

    @Override
    public Item updateItem(Item item) {
        checkUser(item.getOwnerId());
        Item itemToUpdate = getItemById(item.getId());
        if (!Objects.equals(itemToUpdate.getOwnerId(), item.getOwnerId())) {
            throw new EntityNotFoundException("Редактировать вещь может только владелец");
        }
        Item updatedItem = itemDao.updateItem(item);
        log.info("Вещь c id {} была обновлена", updatedItem.getId());
        return updatedItem;
    }

    @Override
    public void deleteItem(Long itemId, Long ownerId) {
        Item itemToDelete = getItemById(itemId);
        if (!Objects.equals(itemToDelete.getOwnerId(), ownerId)) {
            throw new EntityNotFoundException("Удалить вещь может только владелец");
        }
        itemDao.deleteItem(itemId);
        log.info("Вещь c id {} была удалена", itemId);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemDao.searchByNameAndDescription(text);
    }

    private void checkUser(Long ownerId) {
        userDao.getUserById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Владелец вещи с id %d не найден", ownerId)));
    }
}
