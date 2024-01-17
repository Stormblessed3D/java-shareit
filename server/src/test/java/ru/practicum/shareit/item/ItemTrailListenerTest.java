package ru.practicum.shareit.item;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ru.practicum.shareit.item.model.Item;

class ItemTrailListenerTest {
    private final ItemTrailListener itemTrailListener = new ItemTrailListener();
    private Item item;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .build();
    }

    @Test
    void beforeAnyUpdate_whenInvoked_thenLogUpdateOrDelete() {
        Logger itemTrailListenerLogger = (Logger) LoggerFactory.getLogger(ItemTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        itemTrailListenerLogger.addAppender(listAppender);

        itemTrailListener.beforeAnyUpdate(item);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[ITEM AUDIT] About to update/delete item: 1", Level.INFO));
    }

    @Test
    void beforeAnyUpdate_whenIdNull_thenLogAddToDatabase() {
        item.setId(null);
        Logger itemTrailListenerLogger = (Logger) LoggerFactory.getLogger(ItemTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        itemTrailListenerLogger.addAppender(listAppender);

        itemTrailListener.beforeAnyUpdate(item);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[ITEM AUDIT] About to add a item to database", Level.INFO));
    }

    @Test
    void beforeAnyUpdate_whenIdZero_thenLogAddToDatabase() {
        item.setId(0L);
        Logger itemTrailListenerLogger = (Logger) LoggerFactory.getLogger(ItemTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        itemTrailListenerLogger.addAppender(listAppender);

        itemTrailListener.beforeAnyUpdate(item);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[ITEM AUDIT] About to add a item to database", Level.INFO));
    }

    @Test
    void afterAnyUpdate_whenInvoked_thenLogAddOrUpdateOrDelete() {
        Logger itemTrailListenerLogger = (Logger) LoggerFactory.getLogger(ItemTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        itemTrailListenerLogger.addAppender(listAppender);

        itemTrailListener.afterAnyUpdate(item);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[ITEM AUDIT] add/update/delete complete for item: 1", Level.INFO));
    }

    @Test
    void afterLoad_whenInvoked_thenLogLoadedFromDatabase() {
        Logger itemTrailListenerLogger = (Logger) LoggerFactory.getLogger(ItemTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        itemTrailListenerLogger.addAppender(listAppender);

        itemTrailListener.afterLoad(item);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[ITEM AUDIT] item loaded from database: 1", Level.INFO));
    }


}