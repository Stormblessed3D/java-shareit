package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> usersById = new HashMap<>();
    private final Map<String, Long> usersByEmail = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    @Override
    public User createUser(User user) {
        user.setId(generateId());
        usersById.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        User userToUpdate = usersById.get(user.getId());
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            usersByEmail.remove(userToUpdate.getEmail());
            usersByEmail.put(user.getEmail(), user.getId());
            userToUpdate.setEmail(user.getEmail());
        }
        return userToUpdate;
    }

    @Override
    public void deleteUser(Long userId) {
        usersById.remove(userId);
        usersByEmail.values().remove(userId);
    }

    @Override
    public boolean isEmailUnique(String email, Long userId) {
        if (userId != null) {
            if (email != null && email.equals(usersById.get(userId).getEmail())) {
                return true;
            }
        }
        return !usersByEmail.containsKey(email);
    }

    private Long generateId() {
        return ++id;
    }
}
