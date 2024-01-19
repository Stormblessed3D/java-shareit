package ru.practicum.shareit.comment;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class CommentDtoRequest {
    private String text;

    public CommentDtoRequest() {
    }

    public CommentDtoRequest(String text) {
        this.text = text;
    }
}
