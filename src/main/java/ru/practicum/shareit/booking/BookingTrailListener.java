package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.model.Booking;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

@Slf4j
public class BookingTrailListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyUpdate(Booking booking) {
        if (booking.getId() == null || booking.getId() == 0) {
            log.info("[BOOKING AUDIT] About to add a booking to database");
        } else {
            log.info("[BOOKING AUDIT] About to update/delete booking: {}", booking.getId());
        }
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    private void afterAnyUpdate(Booking booking) {
        log.info("[BOOKING AUDIT] add/update/delete complete for booking: {}", booking.getId());
    }

    @PostLoad
    private void afterLoad(Booking booking) {
        log.info("[BOOKING AUDIT] booking loaded from database: {}", booking.getId());
    }
}
