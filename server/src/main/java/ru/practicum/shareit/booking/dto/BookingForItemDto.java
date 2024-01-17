package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
public class BookingForItemDto {
    @EqualsAndHashCode.Include
    private Long id;
    private Long bookerId;

    public BookingForItemDto(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
