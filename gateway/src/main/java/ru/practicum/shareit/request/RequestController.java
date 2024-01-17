package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.RequestDtoPost;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.constant.ConstantKeeper.USER_REQUEST_HEADER;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody RequestDtoPost requestDtoPost,
                                                @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return requestClient.createRequest(userId, requestDtoPost);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return requestClient.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable @Positive Long requestId,
                                             @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return requestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllWithPagination(@RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive @Max(value = 100) Integer size,
                                                       @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return requestClient.getRequestsWithPagination(from, size, userId);
    }
}
