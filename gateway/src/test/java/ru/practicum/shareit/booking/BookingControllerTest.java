package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.ConstantKeeper.USER_REQUEST_HEADER;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingClient bookingClient;

    private final BookingDtoReceived bookingDtoReceived = BookingDtoReceived.builder()
            .start(LocalDateTime.now().plusSeconds(5))
            .end(LocalDateTime.now().plusDays(1))
            .itemId(1L)
            .build();

    @Test
    @SneakyThrows
    void getBookingById_whenBookingIdIsNegative_thenThrowValidationException() {
        Long bookingId = -1L;
        Long userId = 1L;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException));

        verify(bookingClient, never()).bookItem(userId, bookingDtoReceived);
    }

    @Test
    @SneakyThrows
    void createBooking_whenStartInPast_thenStatusIsBadRequest() {
        BookingDtoReceived bookingDtoReceived = BookingDtoReceived.builder()
                .start(LocalDateTime.now().minusSeconds(20))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();
        Long userId = 1L;

        mockMvc.perform(post("/bookings")
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoReceived)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(userId, bookingDtoReceived);
    }

    @Test
    @SneakyThrows
    void createBooking_whenEndIsBeforeStart_thenStatusIsBadRequest() {
        BookingDtoReceived bookingDtoReceived = BookingDtoReceived.builder()
                .start(LocalDateTime.now().plusSeconds(20))
                .end(LocalDateTime.now().minusDays(1))
                .itemId(1L)
                .build();

        Long userId = 1L;

        mockMvc.perform(post("/bookings")
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoReceived)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(userId, bookingDtoReceived);
    }

    @Test
    @SneakyThrows
    void createBooking_whenItemIdIsNull_thenStatusIsBadRequest() {
        BookingDtoReceived bookingDtoReceived = BookingDtoReceived.builder()
                .start(LocalDateTime.now().plusSeconds(20))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(null)
                .build();

        Long userId = 1L;

        mockMvc.perform(post("/bookings")
                        .header(USER_REQUEST_HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoReceived)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).bookItem(userId, bookingDtoReceived);
    }

    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenInvalidParams_thenResponseStatusIsBadRequest() {
        Long userId = 1L;

        mockMvc.perform(get("/bookings")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookings(anyLong(), eq(BookingState.ALL), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenSizeParamOver100_thenResponseStatusIsBadRequest() {
        Long userId = 1L;

        mockMvc.perform(get("/bookings")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookings(anyLong(), eq(BookingState.ALL), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getAllBookingsByOwner_whenInvalidParams_thenResponseStatusIsBadRequest() {
        Long userId = 1L;

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookings(anyLong(), eq(BookingState.ALL), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getAllBookingsByOwner_whenSizeParamOver100_thenResponseStatusIsBadRequest() {
        Long userId = 1L;

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_REQUEST_HEADER, userId)
                        .param("from", "0")
                        .param("size", "101"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookings(anyLong(), eq(BookingState.ALL), anyInt(), anyInt(), anyBoolean());
    }
}