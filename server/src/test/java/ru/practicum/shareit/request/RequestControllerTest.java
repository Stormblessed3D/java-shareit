package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.RequestDtoPost;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.ConstantKeeper.USER_REQUEST_HEADER;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RequestService requestService;
    private final RequestDtoPost requestDtoPost = RequestDtoPost.builder()
            .description("description")
            .build();

    @Test
    @SneakyThrows
    void createRequest_whenInvokedWithValidDtoReceived_thenRequestIsSaved() {
        Long userId = 1L;
        RequestDtoResponse expectedDtoResponse = new RequestDtoResponse();
        when(requestService.createRequest(requestDtoPost, userId)).thenReturn(expectedDtoResponse);

        String response = mockMvc.perform(post("/requests")
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDtoPost)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).createRequest(requestDtoPost, userId);
        assertEquals(objectMapper.writeValueAsString(expectedDtoResponse), response);
    }

    @Test
    @SneakyThrows
    void createRequest_whenDescriptionIsBlank_thenStatusIsBadRequest() {
        RequestDtoPost requestDtoPost = RequestDtoPost.builder()
                .description("")
                .build();
        Long userId = 1L;

        mockMvc.perform(post("/requests")
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDtoPost)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).createRequest(requestDtoPost, userId);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenInvoked_thenResponseStatusOkWithListOfRequestDtoInBody() {
        Long userId = 1L;
        List<RequestDtoResponse> expectedDtoResponses = List.of(new RequestDtoResponse());
        when(requestService.getAllRequests(anyLong())).thenReturn(expectedDtoResponses);

        String response = mockMvc.perform(get("/requests")
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).getAllRequests(anyLong());
        assertEquals(objectMapper.writeValueAsString(expectedDtoResponses), response);
    }

    @Test
    @SneakyThrows
    void getRequestById_whenInvokedWithValidRequestIdAndUserId_thenResponseStatusOkWithDtoInBody() {
        Long requestId = 1L;
        Long userId = 1L;
        RequestDtoResponse expectedDtoResponse = new RequestDtoResponse();
        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(expectedDtoResponse);

        String response = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).getRequestById(anyLong(), anyLong());
        assertEquals(objectMapper.writeValueAsString(expectedDtoResponse), response);
    }

    @Test
    @SneakyThrows
    void getRequestById_whenRequestIdIsInvalid_thenResponseStatusIsBadRequestAndThrowEntityNotFoundException() {
        Long requestId = 1000000L;
        Long userId = 1L;
        when(requestService.getRequestById(anyLong(), anyLong())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));

        verify(requestService).getRequestById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getRequestById_whenRequestIdIsNegative_thenResponseStatusIsBadRequest() {
        Long requestId = -1L;
        Long userId = 1L;

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getRequestById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllWithPagination_whenInvokedWithDefaultParams_thenResponseStatusOkWithListOfRequestDtoInBody() {
        Long userId = 1L;
        List<RequestDtoResponse> expectedDtoResponses = List.of(new RequestDtoResponse());
        when(requestService.getAllByPages(anyInt(), anyInt(), anyLong())).thenReturn(expectedDtoResponses);

        String response = mockMvc.perform(get("/requests/all")
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).getAllByPages(anyInt(), anyInt(), anyLong());
        assertEquals(objectMapper.writeValueAsString(expectedDtoResponses), response);
    }

    @Test
    @SneakyThrows
    void getAllWithPagination_whenInvalidParams_thenResponseStatusIsBadRequest() {
        Long userId = 1L;

        mockMvc.perform(get("/requests/all")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getAllByPages(anyInt(), anyInt(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllWithPagination_whenSizeParamOver100_thenResponseStatusIsBadRequest() {
        Long userId = 1L;
        List<RequestDtoResponse> expectedDtoResponses = List.of(new RequestDtoResponse());
        when(requestService.getAllByPages(anyInt(), anyInt(), anyLong())).thenReturn(expectedDtoResponses);

        mockMvc.perform(get("/requests/all")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getAllByPages(anyInt(), anyInt(), anyLong());
    }
}