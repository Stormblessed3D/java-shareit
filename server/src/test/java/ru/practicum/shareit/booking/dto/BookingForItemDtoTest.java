package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingForItemDtoTest {
    @Autowired
    private JacksonTester<BookingForItemDto> bookingJacksonTester;

    @Test
    @SneakyThrows
    void testSerialization() {
        BookingForItemDto bookingForItemDto = BookingForItemDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        JsonContent<BookingForItemDto> jsonContent = bookingJacksonTester.write(bookingForItemDto);
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingForItemDto.getId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingForItemDto.getBookerId().intValue());
    }

}