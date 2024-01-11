package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"requestor"})
    List<Request> findByRequestorId(Long userId, Sort sort);

    @Query(value = "SELECT r FROM Request r ORDER BY r.created DESC limit ?2 offset ?1 ", nativeQuery = true)
    List<Request> findAllByPage(int limit, int offset);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"requestor"})
    Page<Request> findAllByRequestorIdNot(Long requestorId, Pageable pageable);
}
