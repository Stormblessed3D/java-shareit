package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.user.dto.UserForBookingDto;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
public class BookingDtoToReturn {
    @EqualsAndHashCode.Include
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserForBookingDto booker;
    private ItemForBookingDto item;

    public BookingDtoToReturn(Long id, LocalDateTime start, LocalDateTime end, BookingStatus status,
                              UserForBookingDto booker, ItemForBookingDto item) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.booker = booker;
        this.item = item;
    }

    @JsonCreator
    public BookingDtoToReturn(@JsonProperty("id") Long id, @JsonProperty("start") LocalDateTime start,
                              @JsonProperty("end") LocalDateTime end, @JsonProperty("status") BookingStatus status,
                              @JsonProperty("booker") UserForBookingDto booker) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.booker = booker;
    }
}
