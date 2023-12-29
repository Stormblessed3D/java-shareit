package ru.practicum.shareit.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDtoResponse toCommentDto(Comment comment) {
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDtoResponse> toCommentDto(Iterable<Comment> comments) {
        List<CommentDtoResponse> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(toCommentDto(comment));
        }
        return dtos;
    }

    public static Comment toComment(CommentDtoRequest commentDtoRequest, Item item, User author) {
        return Comment.builder()
                .text(commentDtoRequest.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }
}
