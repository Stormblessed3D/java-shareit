package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "insert into comments(text, item_id, author_id, created) values(?1, ?2, ?3, ?4)", nativeQuery = true)
    Comment saveComment(String text, Long itemId, Long authorId, LocalDateTime created);
}
