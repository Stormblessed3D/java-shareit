package ru.practicum.shareit.comment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoRequestTest {
    @Autowired
    private JacksonTester<CommentDtoRequest> commentJacksonTester;

    @Test
    @SneakyThrows
    void testSerialization() {
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder()
                .text("text")
                .build();

        JsonContent<CommentDtoRequest> jsonContent = commentJacksonTester.write(commentDtoRequest);
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDtoRequest.getText());
    }
}