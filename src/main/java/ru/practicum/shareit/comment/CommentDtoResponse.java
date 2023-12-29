package ru.practicum.shareit.comment;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class CommentDtoResponse {
    @EqualsAndHashCode.Include
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

    public CommentDtoResponse() {
    }

    public CommentDtoResponse(Long id, String text, String authorName, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;
        this.created = created;
    }
}
