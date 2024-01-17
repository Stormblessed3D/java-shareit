package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class CommentDtoRequest {
    @NotBlank
    @Size(max = 255)
    private String text;

    public CommentDtoRequest() {
    }

    public CommentDtoRequest(String text) {
        this.text = text;
    }
}
