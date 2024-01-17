package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@Slf4j
public class ItemTrailListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    protected void beforeAnyUpdate(Item item) {
        if (item.getId() == null || item.getId() == 0) {
            log.info("[ITEM AUDIT] About to add a item to database");
        } else {
            log.info("[ITEM AUDIT] About to update/delete item: {}", item.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    protected void afterAnyUpdate(Item item) {
        log.info("[ITEM AUDIT] add/update/delete complete for item: {}", item.getId());
    }

    @PostLoad
    protected void afterLoad(Item item) {
        log.info("[ITEM AUDIT] item loaded from database: {}", item.getId());
    }
}
