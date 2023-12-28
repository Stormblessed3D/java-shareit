package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    Iterable<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where i.owner.id = ?1 " +
            "order by bkng.start desc")
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.start < ?2) and (bkng.end > ?3)" +
            "order by bkng.start desc")
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime now1,
                                                                                LocalDateTime now2);

    @Query("select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.end < ?2)" +
            "order by bkng.start desc")
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    @Query("select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.start > ?2)" +
            "order by bkng.start desc")
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    @Query("select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.status = ?2)" +
            "order by bkng.start desc")
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @Query(value = "select count(*) " +
            "from bookings as b " +
            "where (b.item_id = ?1) and ((b.start_date between ?2 and ?3) or ((b.end_date between ?2 and ?3))", nativeQuery = true)
    Long countBookingsWithOverlap(Long itemId, LocalDateTime startTime, LocalDateTime endTime);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findAllByItemIdAndEndAfterOrderByStartDesc(Long itemId, LocalDateTime now);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime now);
}