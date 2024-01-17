package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoRequestTest {
    @Autowired
    private JacksonTester<ItemDtoRequest> itemJacksonTester;

    @Test
    @SneakyThrows
    void testSerialization() {
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemDtoRequest> jsonContent = itemJacksonTester.write(itemDtoRequest);
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) itemDtoRequest.getId());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDtoRequest.getName());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDtoRequest.getDescription());
        assertThat(jsonContent)
                .extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDtoRequest.getAvailable());
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDtoRequest.getRequestId().intValue());
    }
}