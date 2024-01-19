package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
public class ItemDtoResponseWithRequestId {
    @EqualsAndHashCode.Include
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

    public ItemDtoResponseWithRequestId(long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
