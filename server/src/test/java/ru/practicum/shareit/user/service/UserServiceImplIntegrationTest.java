package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {
    private final UserService userService;
    private final EntityManager em;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("user1")
                .email("user1@gmail.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@gmail.com")
                .build();
    }

    @Test
    void getUsers_whenUserIsSaved_thenListOfUserDtoIsReturned() {
        em.persist(user);
        em.flush();
        UserDto expectedUser = UserMapper.toUserDto(user);

        List<UserDto> actualUsers = userService.getUsers();

        assertNotNull(actualUsers);
        assertThat(actualUsers.size(), equalTo(1));
        assertThat(actualUsers.get(0).getId(), equalTo(1L));
        assertThat(actualUsers.get(0).getName(), equalTo(expectedUser.getName()));
        assertThat(actualUsers.get(0).getEmail(), equalTo(expectedUser.getEmail()));
    }

    @Test
    void createUser() {
        UserDto actualUser = userService.createUser(userDto);

        assertNotNull(actualUser);
        assertThat(actualUser.getId(), equalTo(1L));
        assertThat(actualUser.getName(), equalTo("user1"));
        assertThat(actualUser.getEmail(), equalTo("user1@gmail.com"));
    }
}