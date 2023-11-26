package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class ItemRequestDto {
    @EqualsAndHashCode.Include
    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;

    public ItemRequestDto(long id, String description, User requestor, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }
}
