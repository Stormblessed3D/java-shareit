package ru.practicum.shareit.comment;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class CommentDto {
    @EqualsAndHashCode.Include
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;

    public CommentDto() {
    }

    public CommentDto(Long id, String text, String authorName, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;
        this.created = created;
    }
}
