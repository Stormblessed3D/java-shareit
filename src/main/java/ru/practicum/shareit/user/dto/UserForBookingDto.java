package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class UserForBookingDto {
    @EqualsAndHashCode.Include
    private Long id;

    public UserForBookingDto(long id) {
        this.id = id;
    }
}
