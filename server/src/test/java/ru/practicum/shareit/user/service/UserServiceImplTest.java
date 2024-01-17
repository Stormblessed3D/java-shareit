package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("user_name")
                .email("userEmail@gmail.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("userDto_name")
                .email("userDtoEmail@gmail.com")
                .build();
    }

    @Test
    void getUsers_whenInvoked_thenUserDtoListIsReturned() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> actualUsers = userService.getUsers();

        assertNotNull(actualUsers);
        assertThat(actualUsers.size(), equalTo(1));
        assertThat(actualUsers.get(0).getId(), equalTo(user.getId()));
        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_whenUserFound_thenUserDtoIsReturned() {
        Long userId = user.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto actualUser = userService.getUserById(userId);

        assertNotNull(actualUser);
        assertThat(actualUser.getId(), equalTo(user.getId()));
        verify(userRepository).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_whenUserNotFound_thenEntityNotFoundExceptionIsThrown() {
        Long userId = user.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));

        verify(userRepository).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUser_whenInvoked_thenUserIsSavedAndUserDtoIsReturned() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto actualUser = userService.createUser(userDto);

        assertNotNull(actualUser);
        assertThat(actualUser.getId(), equalTo(user.getId()));
        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_whenUserIsFound_thenUserUpdatedWithNonNullUserDtoFields() {
        Long userId = user.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto actualUser = userService.updateUser(userDto, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser  = userArgumentCaptor.getValue();

        assertThat(savedUser.getId(), equalTo(userDto.getId()));
        assertThat(savedUser.getName(), equalTo("userDto_name"));
        assertThat(savedUser.getEmail(), equalTo("userDtoEmail@gmail.com"));
        verify(userRepository).findById(anyLong());
        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_whenUserDtoNameIsNull_thenUserNameIsNotUpdated() {
        Long userId = user.getId();
        userDto.setName(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto actualUser = userService.updateUser(userDto, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser  = userArgumentCaptor.getValue();

        assertThat(savedUser.getId(), equalTo(userDto.getId()));
        assertThat(savedUser.getName(), equalTo("user_name"));
        assertThat(savedUser.getEmail(), equalTo("userDtoEmail@gmail.com"));
        verify(userRepository).findById(anyLong());
        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_whenUserDtoEmailIsNull_thenUserEmailIsNotUpdated() {
        Long userId = user.getId();
        userDto.setEmail(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto actualUser = userService.updateUser(userDto, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser  = userArgumentCaptor.getValue();

        assertThat(savedUser.getId(), equalTo(userDto.getId()));
        assertThat(savedUser.getName(), equalTo("userDto_name"));
        assertThat(savedUser.getEmail(), equalTo("userEmail@gmail.com"));
        verify(userRepository).findById(anyLong());
        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_whenUserIsNotFound_thenEntityNotFoundExceptionIsThrown() {
        Long userId = user.getId();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userDto, userId));

        verify(userRepository).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_whenUserExists_thenUserIsDeleted() {
        Long userId = user.getId();
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).existsById(anyLong());
        verify(userRepository).deleteById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_whenUserExists_thenEntityNotFoundExcetionIsThrown() {
        Long userId = user.getId();
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository).existsById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}