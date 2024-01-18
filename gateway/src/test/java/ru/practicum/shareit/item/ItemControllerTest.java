package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.ConstantKeeper.USER_REQUEST_HEADER;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemClient itemClient;

    private final ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .requestId(1L)
            .build();

    @Test
    @SneakyThrows
    void getItems_whenInvalidParams_thenResponseStatusIsBadRequest() {
        Long userId = 1L;

        mockMvc.perform(get("/items")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getItems_whenSizeParamOver100_thenResponseStatusIsBadRequest() {
        Long userId = 1L;

        mockMvc.perform(get("/items")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void search_whenInvalidParams_thenResponseStatusOkWithListOfItemDtoInBody() {
        Long userId = 1L;

        mockMvc.perform(get("/items/search")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("text", "")
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).search(anyString(), anyInt(), anyInt(), anyLong());
    }

    @Test
    @SneakyThrows
    void createItem_whenNameIsBlank_thenStatusIsBadRequest() {
        ItemDtoRequest invalidItemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();
        Long userId = 1L;

        mockMvc.perform(post("/items")
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidItemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, itemDtoRequest);
    }

    @Test
    @SneakyThrows
    void createItem_whenDescriptionIsBlank_thenStatusIsBadRequest() {
        ItemDtoRequest invalidItemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("name")
                .description("")
                .available(true)
                .requestId(1L)
                .build();
        Long userId = 1L;

        mockMvc.perform(post("/items")
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidItemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, itemDtoRequest);
    }

    @Test
    @SneakyThrows
    void createItem_whenAvailableIsNull_thenStatusIsBadRequest() {
        ItemDtoRequest invalidItemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(null)
                .requestId(1L)
                .build();
        Long userId = 1L;

        mockMvc.perform(post("/items")
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidItemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, itemDtoRequest);
    }

    @Test
    @SneakyThrows
    void createItem_whenRequestIdIsNegative_thenStatusIsBadRequest() {
        ItemDtoRequest invalidItemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(-100L)
                .build();
        Long userId = 1L;

        mockMvc.perform(post("/items")
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidItemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(userId, itemDtoRequest);
    }

    @Test
    @SneakyThrows
    void updateItem_whenItemIdIsNegative_thenResponseStatusIsBadRequest() {
        Long itemId = -1L;
        Long userId = 1L;

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(userId, itemId, itemDtoRequest);
    }

    @Test
    @SneakyThrows
    void updateItem_whenNameSizeIsOver255Symbols_thenResponseStatusIsBadRequest() {
        Long itemId = -1L;
        Long userId = 1L;
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("name")
                .description("d".repeat(256))
                .available(true)
                .requestId(1L)
                .build();

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(userId, itemId, itemDtoRequest);
    }

    @Test
    @SneakyThrows
    void createComment_whenCommentTextIsBlank_thenResponseStatusBadRequest() {
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("")
                .build();
        Long itemId = 1L;
        Long userId = 1L;

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDtoRequest)))
                .andExpect(status().isBadRequest());
    }
}