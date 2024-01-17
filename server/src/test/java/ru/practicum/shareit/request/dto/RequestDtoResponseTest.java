package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithRequestId;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;

@JsonTest
class RequestDtoResponseTest {
    @Autowired
    private JacksonTester<RequestDtoResponse> requestJacksonTester;

    @Test
    @SneakyThrows
    void testSerialization() {
        RequestDtoResponse requestDtoResponse = RequestDtoResponse.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .items(List.of(new ItemDtoResponseWithRequestId()))
                .build();

        JsonContent<RequestDtoResponse> jsonContent = requestJacksonTester.write(requestDtoResponse);
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo((int) requestDtoResponse.getId());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo(requestDtoResponse.getDescription());
        assertThat(jsonContent)
                .extractingJsonPathArrayValue("$.items")
                .isNotNull();
    }
}