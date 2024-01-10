package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoPost;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static RequestDtoResponse toRequestDtoResponse(Request request) {
        return RequestDtoResponse.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public static RequestDtoResponse toRequestDtoResponse(Request request, List<Item> items) {
        return RequestDtoResponse.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(ItemMapper.toItemDtoResponseWithRequestId(items))
                .build();
    }

    public static Request toRequest(RequestDtoPost request, User requestor, LocalDateTime created) {
        return Request.builder()
                .description(request.getDescription())
                .requestor(requestor)
                .created(created)
                .build();
    }
}
