package ru.practicum.shareit.comment;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@Slf4j
public class CommentTrailListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyUpdate(Comment comment) {
        if (comment.getId() == null || comment.getId() == 0) {
            log.info("[COMMENT AUDIT] About to add a comment to database");
        } else {
            log.info("[COMMENT AUDIT] About to update/delete comment: {}", comment.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    private void afterAnyUpdate(Comment comment) {
        log.info("[COMMENT AUDIT] add/update/delete complete for comment: {}", comment.getId());
    }

    @PostLoad
    private void afterLoad(Comment comment) {
        log.info("[COMMENT AUDIT] comment loaded from database: {}", comment.getId());
    }
}
