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
import ru.practicum.shareit.request.dto.RequestDtoResponse;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private static final String USER_REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<RequestDtoResponse> createRequest(@Valid @RequestBody RequestDtoPost requestDtoPost,
                                                            @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(requestService.createRequest(requestDtoPost, userId));
    }

    @GetMapping
    public ResponseEntity<List<RequestDtoResponse>> getAllRequests(@RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(requestService.getAllRequests(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDtoResponse> getRequestById(@PathVariable @Positive Long requestId,
                                                             @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(requestService.getRequestById(requestId, userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDtoResponse>> getAllWithPagination(@RequestParam(defaultValue = "0") @Min(value = 0) Integer from,
                                                                         @RequestParam(defaultValue = "10") @Positive @Max(value = 100) Integer size,
                                                                         @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(requestService.getAllByPages(from, size, userId));
    }
}
