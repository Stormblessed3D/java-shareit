package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingDtoToReturn;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingStatusState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(properties = {"db.name=test"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {
    private final BookingService bookingService;
    private final EntityManager em;
    private User user;
    private User owner;
    private Item item;
    private Request request;
    private Booking booking;
    BookingDtoReceived bookingDtoReceived;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("owner")
                .email("owner@gmail.com")
                .build();

        user = User.builder()
                .name("user1")
                .email("user1@gmail.com")
                .build();

        request = Request.builder()
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now().plusDays(2))
                .build();

        item = Item.builder()
                .name("item1_name")
                .description("item1_description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        booking = Booking.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        bookingDtoReceived = BookingDtoReceived.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .itemId(1L)
                .build();
    }

    @Test
    void createBooking() {
        Long userId = 2L;
        em.persist(owner);
        em.persist(user);
        em.persist(request);
        em.persist(item);
        em.flush();

        BookingDtoToReturn actualBooking = bookingService.createBooking(bookingDtoReceived, userId);

        assertNotNull(actualBooking);
        assertThat(actualBooking.getId(), equalTo(1L));
        assertThat(actualBooking.getStart(), notNullValue());
        assertThat(actualBooking.getEnd(), notNullValue());
        assertThat(actualBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(actualBooking.getBooker().getId(), equalTo(user.getId()));
        assertThat(actualBooking.getItem().getId(), equalTo(item.getId()));
    }

    @Test
    void getAllBookingsByUser() {
        Long userId = 2L;
        Integer from = 0;
        Integer size = 10;
        em.persist(owner);
        em.persist(user);
        em.persist(request);
        em.persist(item);
        em.persist(booking);
        em.flush();
        List<BookingDtoToReturn> expectedBookings = BookingMapper.toBookingDtoToReturn(List.of(booking));

        List<BookingDtoToReturn> actualBookings = bookingService.getAllBookingsByUser(userId, BookingStatusState.WAITING,
                from, size);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(1));
        assertThat(actualBookings.get(0).getId(), equalTo(1L));
        assertThat(actualBookings.get(0).getStart(), notNullValue());
        assertThat(actualBookings.get(0).getEnd(), notNullValue());
        assertThat(actualBookings.get(0).getStatus(), equalTo(expectedBookings.get(0).getStatus()));
        assertThat(actualBookings.get(0).getBooker().getId(), equalTo(user.getId()));
        assertThat(actualBookings.get(0).getItem().getId(), equalTo(item.getId()));
    }
}