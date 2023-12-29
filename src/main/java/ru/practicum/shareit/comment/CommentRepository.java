package ru.practicum.shareit.comment;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "author"})
    List<Comment> findByItemIn(List<Item> items, Sort sort);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "author"})
    List<Comment> findByItem(Item item);
}
