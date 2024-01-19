package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    @Cacheable(cacheNames = "users", key = "#userId")
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CachePut(cacheNames = "users", key = "#userId")
    public UserDto updateUser(UserDto userDto, Long userId) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId)));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            userToUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            userToUpdate.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(userToUpdate));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(cacheNames = "users", key = "#userId")
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        userRepository.deleteById(userId);
    }
}
