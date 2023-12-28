package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class BookingDtoReceived {
    @EqualsAndHashCode.Include
    private long id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
    private Long itemId;

    public BookingDtoReceived(long id, LocalDateTime start, LocalDateTime end, Long itemId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
    }
}
