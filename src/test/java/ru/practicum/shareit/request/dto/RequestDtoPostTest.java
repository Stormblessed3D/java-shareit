package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoPostTest {
    @Autowired
    private JacksonTester<RequestDtoPost> requestJacksonTester;

    @Test
    @SneakyThrows
    void testDeSerialization() {
        String jsonValue = "{\"description\":\"request_description\"}";
        RequestDtoPost expectedRequestDtoPost = RequestDtoPost.builder()
                .description("request_description")
                .build();

        RequestDtoPost deserializedRequest = requestJacksonTester.parseObject(jsonValue);

        assertThat(deserializedRequest.getDescription()).isEqualTo(expectedRequestDtoPost.getDescription());
    }
}