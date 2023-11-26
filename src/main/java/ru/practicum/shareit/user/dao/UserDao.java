package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> getUsers();

    Optional<User> getUserById(Long userId);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);

    boolean isEmailUnique(String email, Long userId);
}
