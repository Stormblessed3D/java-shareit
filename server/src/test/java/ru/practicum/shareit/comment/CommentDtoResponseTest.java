package ru.practicum.shareit.comment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoResponseTest {
    @Autowired
    private JacksonTester<CommentDtoResponse> commentJacksonTester;

    @Test
    @SneakyThrows
    void testDeSerialization() {
        String jsonValue = "{\"id\":\"1\",\"authorName\":\"name\",\"text\":\"text\"}";
        CommentDtoResponse expectedComment = CommentDtoResponse.builder()
                .id(1L)
                .authorName("name")
                .text("text")
                .created(LocalDateTime.now())
                .build();

        CommentDtoResponse deserializedComment = commentJacksonTester.parseObject(jsonValue);

        assertThat(deserializedComment.getId()).isEqualTo(expectedComment.getId());
        assertThat(deserializedComment.getAuthorName()).isEqualTo(expectedComment.getAuthorName());
        assertThat(deserializedComment.getText()).isEqualTo(expectedComment.getText());
    }
}