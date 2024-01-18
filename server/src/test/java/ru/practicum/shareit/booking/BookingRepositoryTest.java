package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Item item1;
    private Item item2;
    private User owner1;
    private User owner2;
    private User booker1;
    private User booker2;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        owner1 = User.builder()
                .id(1L)
                .name("owner1")
                .email("owner1@gmail.com")
                .build();

        owner2 = User.builder()
                .id(2L)
                .name("owner2")
                .email("owner2@gmail.com")
                .build();

        booker1 = User.builder()
                .id(3L)
                .name("booker1")
                .email("booker1@gmail.com")
                .build();

        booker2 = User.builder()
                .id(4L)
                .name("booker2")
                .email("booker2@gmail.com")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("item1_name")
                .description("item1_description")
                .available(true)
                .owner(owner1)
                .request(null)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name("item2_name")
                .description("item2_description")
                .available(true)
                .owner(owner2)
                .request(null)
                .build();

        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusDays(1))
                .item(item1)
                .booker(booker1)
                .status(BookingStatus.WAITING)
                .build();

        booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusSeconds(55))
                .end(LocalDateTime.now().plusDays(3))
                .item(item2)
                .booker(booker2)
                .status(BookingStatus.APPROVED)
                .build();

        userRepository.save(owner1);
        userRepository.save(owner2);
        userRepository.save(booker1);
        userRepository.save(booker2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
    }

    @Test
    void findAllByItemOwnerIdOrderByStart() {
        Long userId = owner1.getId();

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStart(userId,
                PageRequest.ofSize(10)).getContent();

        assertNotNull(bookings);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookings.get(0).getItem().getId(), equalTo(item1.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker1.getId()));
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart() {
        Long userId = owner1.getId();

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart(userId,
                LocalDateTime.now().plusSeconds(7), LocalDateTime.now().plusSeconds(50),
                PageRequest.ofSize(10)).getContent();

        assertNotNull(bookings);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookings.get(0).getItem().getId(), equalTo(item1.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker1.getId()));
    }

    @Test
    void findAllByItemOwnerIdAndEndBeforeOrderByStart() {
        Long userId = owner1.getId();

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStart(userId,
                LocalDateTime.now().plusDays(5), PageRequest.ofSize(10)).getContent();

        assertNotNull(bookings);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookings.get(0).getItem().getId(), equalTo(item1.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker1.getId()));
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStart() {
        Long userId = owner1.getId();

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStart(userId,
                LocalDateTime.now().plusSeconds(1), PageRequest.ofSize(10)).getContent();

        assertNotNull(bookings);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookings.get(0).getItem().getId(), equalTo(item1.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker1.getId()));
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByStart() {
        Long userId = owner1.getId();

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStart(userId,
                BookingStatus.WAITING, PageRequest.ofSize(10)).getContent();

        assertNotNull(bookings);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo(booking1.getId()));
        assertThat(bookings.get(0).getItem().getId(), equalTo(item1.getId()));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker1.getId()));
    }

    @Test
    void countByItemIdAndBookerIdAndEndBefore() {
        Long userId = owner1.getId();
        Long bookerId = booker1.getId();
        Long count = bookingRepository.countByItemIdAndBookerIdAndEndBefore(userId, bookerId,
                LocalDateTime.now().plusDays(2));

        assertThat(count, equalTo(1L));
    }
}