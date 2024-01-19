package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.model.BookingStatusState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private BookingService bookingService;

    private final BookingDtoReceived bookingDtoReceived = BookingDtoReceived.builder()
            .start(LocalDateTime.now().plusSeconds(5))
            .end(LocalDateTime.now().plusDays(1))
            .itemId(1L)
            .build();

    @Test
    @SneakyThrows
    void getBookingById_whenInvokedWithValidBookingIdAndUserId_thenResponseStatusOkWithBookingDtoInBody() {
        Long bookingId = 1L;
        Long userId = 1L;
        BookingDtoToReturn expectedBooking = new BookingDtoToReturn();
        when(bookingService.getBookingById(bookingId, userId)).thenReturn(expectedBooking);

        String response = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBookingById(bookingId, userId);
        assertEquals(objectMapper.writeValueAsString(expectedBooking), response);
    }

    @Test
    @SneakyThrows
    void getBookingById_whenBookingIdIsInvalid_thenResponseStatusIsBadRequestAndThrowEntityNotFoundException() {
        Long bookingId = 1000000L;
        Long userId = 1L;
        when(bookingService.getBookingById(bookingId, userId)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));

        verify(bookingService).getBookingById(bookingId, userId);
    }

    @Test
    @SneakyThrows
    void createBooking_whenInvokedWithValidDtoReceived_thenBookingIsSaved() {
        Long userId = 1L;
        BookingDtoToReturn expectedBooking = new BookingDtoToReturn();
        when(bookingService.createBooking(bookingDtoReceived, userId)).thenReturn(expectedBooking);

        String response = mockMvc.perform(post("/bookings")
                    .header(USER_REQUEST_HEADER, userId)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(bookingDtoReceived)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).createBooking(bookingDtoReceived, userId);
        assertEquals(objectMapper.writeValueAsString(expectedBooking), response);
    }

    @Test
    @SneakyThrows
    void approveBooking_whenInvoked_thenResponseStatusOkWithBookingDtoInBody() {
        Long bookingId = 1L;
        Long userId = 1L;
        BookingDtoToReturn expectedBooking = new BookingDtoToReturn();
        when(bookingService.approveBooking(bookingId, true, userId)).thenReturn(expectedBooking);

        String response = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_REQUEST_HEADER, userId)
                        .param("approved", "true"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        verify(bookingService).approveBooking(bookingId, true, userId);
        assertEquals(objectMapper.writeValueAsString(expectedBooking), response);
    }

    @Test
    @SneakyThrows
    void getAllBookingsByUser_whenInvokedWithValidParams_thenResponseStatusOkWithListOfBookingDtoInBody() {
        Long userId = 1L;
        List<BookingDtoToReturn> expectedBookings = List.of(new BookingDtoToReturn());
        when(bookingService.getAllBookingsByUser(anyLong(), eq(BookingStatusState.ALL), anyInt(),
                anyInt())).thenReturn(expectedBookings);

        String response = mockMvc.perform(get("/bookings")
                        .header(USER_REQUEST_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getAllBookingsByUser(anyLong(), eq(BookingStatusState.ALL), anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedBookings), response);
    }

    @Test
    @SneakyThrows
    void getAllBookingsByOwner_whenInvokedWithValidParams_thenResponseStatusOkWithListOfBookingDtoInBody() {
        Long ownerId = 1L;
        List<BookingDtoToReturn> expectedBookings = List.of(new BookingDtoToReturn());
        when(bookingService.getAllBookingsByOwner(anyLong(), eq(BookingStatusState.ALL), anyInt(),
                anyInt())).thenReturn(expectedBookings);

        String response = mockMvc.perform(get("/bookings/owner")
                        .header(USER_REQUEST_HEADER, ownerId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getAllBookingsByOwner(anyLong(), eq(BookingStatusState.ALL), anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(expectedBookings), response);
    }
}