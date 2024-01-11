package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoReceivedTest {
    @Autowired
    private JacksonTester<BookingDtoReceived> bookingJacksonTester;

    @Test
    @SneakyThrows
    void testSerialization() {
        BookingDtoReceived bookingDtoReceived = BookingDtoReceived.builder()
                .id(1L)
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();

        JsonContent<BookingDtoReceived> jsonContent = bookingJacksonTester.write(bookingDtoReceived);
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) bookingDtoReceived.getId());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingDtoReceived.getItemId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.start")
                .isNotNull();
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.end")
                .isNotNull();
    }
}