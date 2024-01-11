package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDtoPost;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

import java.util.List;

public interface RequestService {
    RequestDtoResponse createRequest(RequestDtoPost requestDtoPost, Long userId);

    List<RequestDtoResponse> getAllRequests(Long userId);

    RequestDtoResponse getRequestById(Long requestId, Long userId);

    List<RequestDtoResponse> getAllByPages(Integer from, Integer size, Long userId);
}
