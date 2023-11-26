package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Item {
    @EqualsAndHashCode.Include
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;

    public Item(long id, String name, String description, Boolean available, Long ownerId, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.ownerId = ownerId;
        this.requestId = requestId;
    }
}
