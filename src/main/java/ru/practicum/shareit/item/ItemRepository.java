package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Override
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"owner", "request"})
    List<Item> findAll();

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"bookings"})
    List<Item> findByOwnerId(Long userId);

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%'))" +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) and (i.available = true) " +
            "order by i.id asc")
    List<Item> findByNameOrDescriptionContainingIgnoreCase(String text);
}
