package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.CommentDtoResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoResponseTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    void testDeSerialization() {
        String jsonValue = "{\"id\":\"1\",\"name\":\"name\",\"description\":\"description\",\"available\":\"true\", " +
                "\"requestId\":\"1\", \"lastBooking\":{\"id\":\"null\",\"bookerId\":\"null\"}, " +
                "\"nextBooking\":{\"id\":\"null\",\"bookerId\":\"null\"}}";
        ItemDtoResponse expectedItem = ItemDtoResponse.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .lastBooking(new BookingForItemDto())
                .nextBooking(new BookingForItemDto())
                .comments(List.of(new CommentDtoResponse()))
                .build();

        ItemDtoResponse dtoObject = mapper.readValue(jsonValue, ItemDtoResponse.class);

        assertThat(dtoObject.getId()).isEqualTo(expectedItem.getId());
        assertThat(dtoObject.getName()).isEqualTo(expectedItem.getName());
        assertThat(dtoObject.getDescription()).isEqualTo(expectedItem.getDescription());
        assertThat(dtoObject.getAvailable()).isEqualTo(expectedItem.getAvailable());
        assertThat(dtoObject.getRequestId()).isEqualTo(expectedItem.getRequestId());
        assertThat(dtoObject.getLastBooking()).isNotNull();
        assertThat(dtoObject.getNextBooking()).isNotNull();
    }
}