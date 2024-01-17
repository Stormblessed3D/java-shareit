package ru.practicum.shareit.comment;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class CommentTrailListenerTest {
    private final CommentTrailListener commentTrailListener = new CommentTrailListener();
    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = Comment.builder()
                .id(1L)
                .build();
    }

    @Test
    void beforeAnyUpdate_whenInvoked_thenLogUpdateOrDelete() {
        Logger commentTrailListenerLogger = (Logger) LoggerFactory.getLogger(CommentTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        commentTrailListenerLogger.addAppender(listAppender);

        commentTrailListener.beforeAnyUpdate(comment);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[COMMENT AUDIT] About to update/delete comment: 1", Level.INFO));
    }

    @Test
    void beforeAnyUpdate_whenCommentIdNull_thenLogAddToDatabase() {
        comment.setId(null);
        Logger commentTrailListenerLogger = (Logger) LoggerFactory.getLogger(CommentTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        commentTrailListenerLogger.addAppender(listAppender);

        commentTrailListener.beforeAnyUpdate(comment);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[COMMENT AUDIT] About to add a comment to database", Level.INFO));
    }

    @Test
    void beforeAnyUpdate_whenCommentIdZero_thenLogAddToDatabase() {
        comment.setId(0L);
        Logger commentTrailListenerLogger = (Logger) LoggerFactory.getLogger(CommentTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        commentTrailListenerLogger.addAppender(listAppender);

        commentTrailListener.beforeAnyUpdate(comment);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[COMMENT AUDIT] About to add a comment to database", Level.INFO));
    }

    @Test
    void afterAnyUpdate_whenInvoked_thenLogAddOrUpdateOrDelete() {
        Logger commentTrailListenerLogger = (Logger) LoggerFactory.getLogger(CommentTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        commentTrailListenerLogger.addAppender(listAppender);

        commentTrailListener.afterAnyUpdate(comment);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[COMMENT AUDIT] add/update/delete complete for comment: 1", Level.INFO));
    }

    @Test
    void afterLoad_whenInvoked_thenLogLoadedFromDatabase() {
        Logger commentTrailListenerLogger = (Logger) LoggerFactory.getLogger(CommentTrailListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        commentTrailListenerLogger.addAppender(listAppender);

        commentTrailListener.afterLoad(comment);

        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple("[COMMENT AUDIT] comment loaded from database: 1", Level.INFO));
    }


}