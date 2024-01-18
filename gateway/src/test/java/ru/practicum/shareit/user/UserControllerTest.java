package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserClient userClient;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("name")
            .email("email@gmail.com")
            .build();

    @Test
    @SneakyThrows
    void updateUser_whenUserIdIsNegative_thenResponseStatusIsBadRequest() {
        Long userId = -1L;

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(userId, userDto);
    }

    @Test
    @SneakyThrows
    void updateUser_whenNameIsOver255Symbols_thenStatusIsBadRequest() {
        UserDto invalidUserDto = UserDto.builder()
                .id(1L)
                .name("name".repeat(256))
                .email("email@gmail.com")
                .build();
        Long userId = 1L;

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(userId, userDto);
    }

    @Test
    @SneakyThrows
    void updateUser_whenEmailIsInvalid_thenStatusIsBadRequest() {
        UserDto invalidUserDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("emailgmail.com")
                .build();
        Long userId = 1L;

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(userId, invalidUserDto);
    }

    @Test
    @SneakyThrows
    void createUser_whenEmailIsInvalid_thenStatusIsBadRequest() {
        UserDto invalidUserDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("emailgmail.com")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(invalidUserDto);
    }

    @Test
    @SneakyThrows
    void createUser_whenNameIsBlank_thenStatusIsBadRequest() {
        UserDto invalidUserDto = UserDto.builder()
                .id(1L)
                .name("")
                .email("email@gmail.com")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(invalidUserDto);
    }

    @Test
    @SneakyThrows
    void createUser_whenEmailIsBlank_thenStatusIsBadRequest() {
        UserDto invalidUserDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(invalidUserDto);
    }

    @Test
    @SneakyThrows
    void createUser_whenNameIsOver255Symbols_thenStatusIsBadRequest() {
        UserDto invalidUserDto = UserDto.builder()
                .id(1L)
                .name("name".repeat(256))
                .email("email@gmail.com")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(invalidUserDto);
    }

    @Test
    @SneakyThrows
    void createUser_whenEmailIsOver255Symbols_thenStatusIsBadRequest() {
        UserDto invalidUserDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com".repeat(256))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(invalidUserDto);
    }

    @SneakyThrows
    @Test
    void deleteUser_whenUserIdIsNegative_thenResponseStatusIsBadRequest() {
        Long userId = -1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).deleteUser(anyLong());
    }
}