package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
public class BookingDtoReceived {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;

    public BookingDtoReceived(LocalDateTime start, LocalDateTime end, Long itemId) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
    }
}
