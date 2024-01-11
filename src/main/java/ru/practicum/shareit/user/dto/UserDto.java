package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.validator.OnCreate;
import ru.practicum.shareit.user.validator.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
public class UserDto {
    @EqualsAndHashCode.Include
    private Long id;
    @NotBlank(groups = OnCreate.class)
    @Size(max = 255, groups = {OnCreate.class, OnUpdate.class})
    private String name;
    @Email(groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(groups = OnCreate.class)
    @Size(max = 255, groups = {OnCreate.class, OnUpdate.class})
    private String email;

    public UserDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
