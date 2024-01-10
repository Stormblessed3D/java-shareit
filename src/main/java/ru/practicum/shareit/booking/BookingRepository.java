package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1,
                                                                             LocalDateTime now2, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    @Query(value = "select bkng " +
                    "from Booking as bkng " +
                    "join fetch bkng.item as i " +
                    "join fetch bkng.booker as booker " +
                    "where i.owner.id = ?1 ",
            countQuery = "select count(bkng) " +
                    "from Booking as bkng " +
                    "join bkng.item as i " +
                    "join bkng.booker as booker " +
                    "where i.owner.id = ?1")
    Page<Booking> findAllByItemOwnerIdOrderByStart(Long ownerId, Pageable pageable);

    @Query(value = "select bkng " +
                    "from Booking as bkng " +
                    "join bkng.item as i " +
                    "join bkng.booker as booker " +
                    "where (i.owner.id = ?1) and (bkng.start < ?2) and (bkng.end > ?3)",
            countQuery = "select count(bkng) " +
                    "from Booking as bkng " +
                    "join bkng.item as i " +
                    "join bkng.booker as booker " +
                    "where (i.owner.id = ?1) and (bkng.start < ?2) and (bkng.end > ?3)")
    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart(Long ownerId, LocalDateTime now1,
                                                                            LocalDateTime now2, Pageable pageable);

    @Query(value = "select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.end < ?2)",
    countQuery = "select count(bkng) " +
            "from Booking as bkng " +
            "join bkng.item as i " +
            "join bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.end < ?2)")
    Page<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStart(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query(value = "select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.start > ?2)",
    countQuery = "select bkng " +
            "from Booking as bkng " +
            "join bkng.item as i " +
            "join bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.start > ?2)")
    Page<Booking> findAllByItemOwnerIdAndStartAfterOrderByStart(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query(value = "select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.status = ?2)",
    countQuery = "select bkng " +
            "from Booking as bkng " +
            "join bkng.item as i " +
            "join bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.start > ?2)")
    Page<Booking> findAllByItemOwnerIdAndStatusOrderByStart(Long ownerId, BookingStatus status, Pageable pageable);

    @Query(value = "select count(*) " +
            "from bookings as b " +
            "where (b.item_id = ?1) and ((b.start_date between ?2 and ?3) or ((b.end_date between ?2 and ?3))", nativeQuery = true)
    Long countBookingsWithOverlap(Long itemId, LocalDateTime startTime, LocalDateTime endTime);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findAllByItemIdAndEndAfterOrderByStartDesc(Long itemId, LocalDateTime now);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime now);

    @Query("select count(*) " +
            "from Booking as b " +
            "join b.item as i " +
            "join b.booker as booker " +
            "where (b.item.id = ?1) and (b.booker.id = ?2) and (b.end < ?3)")
    Long countByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime now);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findByItemInAndStatus(List<Item> items, BookingStatus status, Sort sort);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findByItemAndStatus(Item item, BookingStatus status, Sort sort);
}