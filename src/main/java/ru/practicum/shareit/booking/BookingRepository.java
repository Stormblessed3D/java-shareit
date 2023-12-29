package ru.practicum.shareit.booking;

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
            "where i.owner.id = ?1 ")
    List<Booking> findAllByItemOwnerIdOrderByStart(Long ownerId, Sort sort);

    @Query("select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.start < ?2) and (bkng.end > ?3)")
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart(Long ownerId, LocalDateTime now1,
                                                                            LocalDateTime now2, Sort sort);

    @Query("select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.end < ?2)")
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStart(Long ownerId, LocalDateTime now, Sort sort);

    @Query("select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.start > ?2)")
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStart(Long ownerId, LocalDateTime now, Sort sort);

    @Query("select bkng " +
            "from Booking as bkng " +
            "join fetch bkng.item as i " +
            "join fetch bkng.booker as booker " +
            "where (i.owner.id = ?1) and (bkng.status = ?2)")
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStart(Long ownerId, BookingStatus status, Sort sort);

    @Query(value = "select count(*) " +
            "from bookings as b " +
            "where (b.item_id = ?1) and ((b.start_date between ?2 and ?3) or ((b.end_date between ?2 and ?3))", nativeQuery = true)
    Long countBookingsWithOverlap(Long itemId, LocalDateTime startTime, LocalDateTime endTime);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findAllByItemIdAndEndAfterOrderByStartDesc(Long itemId, LocalDateTime now);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime now);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findByItemInAndStatus(List<Item> items, BookingStatus status, Sort sort);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"item", "booker"})
    List<Booking> findByItemAndStatus(Item item, BookingStatus status);
}