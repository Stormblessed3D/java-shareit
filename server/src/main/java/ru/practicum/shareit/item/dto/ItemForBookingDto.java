package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class ItemForBookingDto {
    @EqualsAndHashCode.Include
    private long id;
    private String name;

    public ItemForBookingDto(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
