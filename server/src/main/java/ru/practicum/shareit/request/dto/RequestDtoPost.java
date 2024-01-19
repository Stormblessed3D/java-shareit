package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
public class RequestDtoPost {
    private String description;

    public RequestDtoPost(String description) {
        this.description = description;
    }
}
