package ru.practicum.shareit.user;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class UserTrailListenerTest {
    private final UserTrailListener userTrailListener = new UserTrailListener();
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();
    }

    @Test
    void beforeAnyUpdate_whenInvoked_thenLogUpdateOrDelete() {
        Logger userTrailListenerLogger = (Logger) LoggerFactory.getLogger(UserTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        userTrailListenerLogger.addAppender(listAppender);

        userTrailListener.beforeAnyUpdate(user);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[USER AUDIT] About to update/delete user: 1", Level.INFO));
    }

    @Test
    void beforeAnyUpdate_whenIdNull_thenLogAddToDatabase() {
        user.setId(null);
        Logger userTrailListenerLogger = (Logger) LoggerFactory.getLogger(UserTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        userTrailListenerLogger.addAppender(listAppender);

        userTrailListener.beforeAnyUpdate(user);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[USER AUDIT] About to add a user to database", Level.INFO));
    }

    @Test
    void beforeAnyUpdate_whenIdZero_thenLogAddToDatabase() {
        user.setId(0L);
        Logger userTrailListenerLogger = (Logger) LoggerFactory.getLogger(UserTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        userTrailListenerLogger.addAppender(listAppender);

        userTrailListener.beforeAnyUpdate(user);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[USER AUDIT] About to add a user to database", Level.INFO));
    }

    @Test
    void afterAnyUpdate_whenInvoked_thenLogAddOrUpdateOrDelete() {
        Logger userTrailListenerLogger = (Logger) LoggerFactory.getLogger(UserTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        userTrailListenerLogger.addAppender(listAppender);

        userTrailListener.afterAnyUpdate(user);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[USER AUDIT] add/update/delete complete for user: 1", Level.INFO));
    }

    @Test
    void afterLoad_whenInvoked_thenLogLoadedFromDatabase() {
        Logger userTrailListenerLogger = (Logger) LoggerFactory.getLogger(UserTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        userTrailListenerLogger.addAppender(listAppender);

        userTrailListener.afterLoad(user);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[USER AUDIT] user loaded from database: 1", Level.INFO));
    }
}