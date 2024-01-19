package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestDtoPost;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    private RequestClient requestClient;
    private final RequestDtoPost requestDtoPost = RequestDtoPost.builder()
            .description("description")
            .build();

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

        verify(requestClient, never()).createRequest(userId, requestDtoPost);
    }

    @Test
    @SneakyThrows
    void getRequestById_whenRequestIdIsNegative_thenResponseStatusIsBadRequest() {
        Long requestId = -1L;
        Long userId = 1L;

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).getRequest(anyLong(), anyLong());
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

        verify(requestClient, never()).getRequestsWithPagination(anyInt(), anyInt(), anyLong());
    }

    @Test
    @SneakyThrows
    void getAllWithPagination_whenSizeParamOver100_thenResponseStatusIsBadRequest() {
        Long userId = 1L;

        mockMvc.perform(get("/requests/all")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).getRequestsWithPagination(anyInt(), anyInt(), anyLong());
    }
}