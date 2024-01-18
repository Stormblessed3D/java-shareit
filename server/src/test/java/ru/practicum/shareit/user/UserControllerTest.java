package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("name")
            .email("email@gmail.com")
            .build();

    @Test
    @SneakyThrows
    void getUsers_whenInvoked_thenResponseStatusOkWithListOfUserDtoInBody() {
        List<UserDto> expectedUsers = List.of(new UserDto());
        when(userService.getUsers()).thenReturn(expectedUsers);

        String response = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getUsers();
        assertEquals(objectMapper.writeValueAsString(expectedUsers), response);
    }

    @Test
    @SneakyThrows
    void getUserById_whenInvokedWithValidUserId_thenResponseStatusOkWithDtoInBody() {
        Long userId = 1L;
        UserDto expectedUser = new UserDto();
        when(userService.getUserById(anyLong())).thenReturn(expectedUser);

        String response = mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).getUserById(anyLong());
        assertEquals(objectMapper.writeValueAsString(expectedUser), response);
    }

    @Test
    @SneakyThrows
    void getUserById_whenUserIdIsInvalid_thenResponseStatusIsBadRequestAndThrowEntityNotFoundException() {
        Long userId = 1000000L;
        when(userService.getUserById(anyLong())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));

        verify(userService).getUserById(anyLong());
    }

    @Test
    @SneakyThrows
    void updateUser_whenInvokedWithValidUserId_thenResponseStatusOkWithListOfUserDtoInBody() {
        Long userId = 1L;
        when(userService.updateUser(userDto, userId)).thenReturn(userDto);

        String response = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).updateUser(userDto, userId);
        assertEquals(objectMapper.writeValueAsString(userDto), response);
    }

    @Test
    @SneakyThrows
    void updateUser_whenUserIdIsInvalid_thenResponseStatusIsBadRequestAndThrowEntityNotFoundException() {
        Long userId = 1000000L;
        when(userService.updateUser(userDto, userId)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));

        verify(userService).updateUser(userDto, userId);
    }

    @SneakyThrows
    @Test
    void createUser_whenInvoked_thenResponseStatusOkWithListOfUserDtoInBody() {
        when(userService.createUser(userDto)).thenReturn(userDto);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).createUser(userDto);
        assertEquals(objectMapper.writeValueAsString(userDto), response);
    }

    @SneakyThrows
    @Test
    void deleteUser_whenInvoked_thenResponseStatusIsNoContent() {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(anyLong());
    }

    @SneakyThrows
    @Test
    void deleteUser_whenUserIdIsInvalid_thenResponseStatusIsBadRequestAndThrowEntityNotFoundException() {
        Long userId = 1000000L;
        doThrow(EntityNotFoundException.class).when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(anyLong());
    }
}