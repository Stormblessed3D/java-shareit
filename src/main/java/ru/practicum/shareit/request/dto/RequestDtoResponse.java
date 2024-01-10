package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithRequestId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
public class RequestDtoResponse {
    @EqualsAndHashCode.Include
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoResponseWithRequestId> items;

    public RequestDtoResponse(long id, String description, LocalDateTime created, List<ItemDtoResponseWithRequestId> items) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.items = items;
    }
}
