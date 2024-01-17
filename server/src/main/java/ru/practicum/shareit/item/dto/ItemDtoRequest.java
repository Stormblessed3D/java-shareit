package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.validator.OnCreate;
import ru.practicum.shareit.user.validator.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
public class ItemDtoRequest {
    @EqualsAndHashCode.Include
    private long id;
    @NotBlank(groups = OnCreate.class)
    @Size(max = 255, groups = {OnCreate.class, OnUpdate.class})
    private String name;
    @NotBlank(groups = OnCreate.class)
    @Size(max = 255, groups = {OnCreate.class, OnUpdate.class})
    private String description;
    @NotNull(groups = OnCreate.class)
    private Boolean available;
    @Positive(groups = {OnCreate.class, OnUpdate.class})
    private Long requestId;

    public ItemDtoRequest(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemDtoRequest(long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemDtoRequest(long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
