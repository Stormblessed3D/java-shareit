package ru.practicum.shareit.booking;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

class BookingTrailListenerTest {
    private final BookingTrailListener bookingTrailListener = new BookingTrailListener();
    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusSeconds(5))
                .end(LocalDateTime.now().plusDays(1))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void beforeAnyUpdate_whenInvoked_thenLogUpdateOrDelete() {
        Logger bookingTrailListenerLogger = (Logger) LoggerFactory.getLogger(BookingTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        bookingTrailListenerLogger.addAppender(listAppender);

        bookingTrailListener.beforeAnyUpdate(booking);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[BOOKING AUDIT] About to update/delete booking: 1", Level.INFO));
    }

    @Test
    void beforeAnyUpdate_whenBookingIdNull_thenLogAddToDatabase() {
        booking.setId(null);
        Logger bookingTrailListenerLogger = (Logger) LoggerFactory.getLogger(BookingTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        bookingTrailListenerLogger.addAppender(listAppender);

        bookingTrailListener.beforeAnyUpdate(booking);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[BOOKING AUDIT] About to add a booking to database", Level.INFO));
    }

    @Test
    void beforeAnyUpdate_whenBookingIdZero_thenLogAddToDatabase() {
        booking.setId(0L);
        Logger bookingTrailListenerLogger = (Logger) LoggerFactory.getLogger(BookingTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        bookingTrailListenerLogger.addAppender(listAppender);

        bookingTrailListener.beforeAnyUpdate(booking);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[BOOKING AUDIT] About to add a booking to database", Level.INFO));
    }

    @Test
    void afterAnyUpdate_whenInvoked_thenLogAddOrUpdateOrDelete() {
        Logger bookingTrailListenerLogger = (Logger) LoggerFactory.getLogger(BookingTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        bookingTrailListenerLogger.addAppender(listAppender);

        bookingTrailListener.afterAnyUpdate(booking);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[BOOKING AUDIT] add/update/delete complete for booking: 1", Level.INFO));
    }

    @Test
    void afterLoad_whenInvoked_thenLogLoadedFromDatabase() {
        Logger bookingTrailListenerLogger = (Logger) LoggerFactory.getLogger(BookingTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        bookingTrailListenerLogger.addAppender(listAppender);

        bookingTrailListener.afterLoad(booking);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[BOOKING AUDIT] booking loaded from database: 1", Level.INFO));
    }
}