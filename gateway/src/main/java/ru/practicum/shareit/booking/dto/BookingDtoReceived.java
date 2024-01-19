package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@ru.practicum.shareit.booking.validation.EndDateAfterStart
@NoArgsConstructor
public class BookingDtoReceived {
    @EqualsAndHashCode.Include
    private long id;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull
    private Long itemId;

    public BookingDtoReceived(long id, LocalDateTime start, LocalDateTime end, Long itemId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.itemId = itemId;
    }
}
