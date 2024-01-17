package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentDtoRequest;
import ru.practicum.shareit.comment.CommentDtoResponse;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    private ItemService itemService;

    private final ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .requestId(1L)
            .build();

    @Test
    @SneakyThrows
    void getItems_whenInvokedWithValidParams_thenResponseStatusOkWithListOfBookingDtoInBody() {
        Long userId = 1L;
        List<ItemDtoResponse> expectedItems = List.of(new ItemDtoResponse());
        when(itemService.getItems(anyLong(), anyInt(), anyInt())).thenReturn(expectedItems);

        String response = mockMvc.perform(get("/items")
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getItems(anyLong(), anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedItems), response);
    }

    @Test
    @SneakyThrows
    void getItems_whenInvalidParams_thenResponseStatusIsBadRequest() {
        Long userId = 1L;

        mockMvc.perform(get("/items")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getItems_whenSizeParamOver100_thenResponseStatusIsBadRequest() {
        Long userId = 1L;
        List<ItemDtoResponse> expectedItems = List.of(new ItemDtoResponse());
        when(itemService.getItems(anyLong(), anyInt(), anyInt())).thenReturn(expectedItems);

        mockMvc.perform(get("/items")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getItemById_whenInvokedWithValidItemIdAndUserId_thenResponseStatusOkWithDtoInBody() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDtoResponse expectedItem = new ItemDtoResponse();
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(expectedItem);

        String response = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getItemById(anyLong(), anyLong());
        assertEquals(objectMapper.writeValueAsString(expectedItem), response);
    }

    @Test
    @SneakyThrows
    void getItemById_whenItemIdIsInvalid_thenResponseStatusIsBadRequestAndThrowEntityNotFoundException() {
        Long itemId = 1000000L;
        Long userId = 1L;
        when(itemService.getItemById(anyLong(), anyLong())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));

        verify(itemService).getItemById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void search_whenInvokedWithValidParams_thenResponseStatusOkWithListOfItemDtoInBody() {
        Long userId = 1L;
        List<ItemDtoResponse> expectedItems = List.of(new ItemDtoResponse());
        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(expectedItems);

        String response = mockMvc.perform(get("/items/search")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).search(anyString(), anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedItems), response);
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

        verify(itemService, never()).search(anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void createItem_whenInvokedWithValidDtoReceived_thenItemIsSaved() {
        Long userId = 1L;
        ItemDtoResponse expectedItem = new ItemDtoResponse();
        when(itemService.createItem(itemDtoRequest, userId)).thenReturn(expectedItem);

        String response = mockMvc.perform(post("/items")
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).createItem(itemDtoRequest, userId);
        assertEquals(objectMapper.writeValueAsString(expectedItem), response);
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

        verify(itemService, never()).createItem(itemDtoRequest, userId);
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

        verify(itemService, never()).createItem(itemDtoRequest, userId);
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

        verify(itemService, never()).createItem(itemDtoRequest, userId);
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

        verify(itemService, never()).createItem(itemDtoRequest, userId);
    }

    @Test
    @SneakyThrows
    void updateItem_whenInvoked_thenResponseStatusOkWithBookingDtoInBody() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDtoResponse expectedItem = new ItemDtoResponse();
        when(itemService.updateItem(itemDtoRequest, itemId, userId)).thenReturn(expectedItem);

        String response = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).updateItem(itemDtoRequest, itemId, userId);
        assertEquals(objectMapper.writeValueAsString(expectedItem), response);
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

        verify(itemService, never()).updateItem(itemDtoRequest, itemId, userId);
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

        verify(itemService, never()).updateItem(itemDtoRequest, itemId, userId);
    }

    @Test
    @SneakyThrows
    void updateItem_whenItemIdIsInvalid_thenResponseStatusIsBadRequestAndThrowEntityNotFoundException() {
        Long itemId = 1000000L;
        Long userId = 1L;
        when(itemService.updateItem(itemDtoRequest, itemId, userId)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));

        verify(itemService).updateItem(itemDtoRequest, itemId, userId);
    }

    @Test
    @SneakyThrows
    void deleteItem_whenInvoked_thenResponseStatusIsNoContent() {
        Long itemId = 1L;
        Long userId = 1L;

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header(USER_REQUEST_HEADER, userId))
                        .andExpect(status().isNoContent());

        verify(itemService).deleteItem(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void deleteItem_whenItemIdIsInvalid_thenResponseStatusIsBadRequestAndThrowEntityNotFoundException() {
        Long itemId = 1000000L;
        Long userId = 1L;
        doThrow(EntityNotFoundException.class).when(itemService).deleteItem(anyLong(), anyLong());

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isNotFound());

        verify(itemService).deleteItem(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void createComment_whenInvoked_thenResponseStatusOkWithCommentDtoInResponseBody() {
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("text")
                .build();
        CommentDtoResponse expectedCommentDtoResponse = new CommentDtoResponse();
        Long itemId = 1L;
        Long userId = 1L;
        when(itemService.createComment(commentDtoRequest, itemId, userId)).thenReturn(expectedCommentDtoResponse);

        String response = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDtoRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).createComment(commentDtoRequest, itemId, userId);
        assertEquals(objectMapper.writeValueAsString(expectedCommentDtoResponse), response);
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