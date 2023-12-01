package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public List<User> getUsers() {
        return userDao.getUsers();
    }

    @Override
    public User getUserById(Long userId) {
        return userDao.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    @Override
    public User createUser(User user) {
        if (!userDao.isEmailUnique(user.getEmail(), null)) {
            throw new InvalidEmailException("email пользователя уже существует");
        }
        User createdUser = userDao.createUser(user);
        log.info("Пользователь c id {} создан", createdUser.getId());
        return createdUser;
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());
        if (!userDao.isEmailUnique(user.getEmail(), user.getId())) {
            throw new InvalidEmailException("email пользователя уже существует");
        }
        User updatedUser = userDao.updateUser(user);
        log.info("Пользователь c id {} обновлен", updatedUser.getId());
        return updatedUser;
    }

    @Override
    public void deleteUser(Long userId) {
        getUserById(userId);
        userDao.deleteUser(userId);
        log.info("Пользователь c id {} был удален", userId);
    }
}
