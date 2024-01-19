package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.RequestDtoPost;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

import java.util.List;

import static ru.practicum.shareit.constant.ConstantKeeper.USER_REQUEST_HEADER;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDtoResponse> createRequest(@RequestBody RequestDtoPost requestDtoPost,
                                                            @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(requestService.createRequest(requestDtoPost, userId));
    }

    @GetMapping
    public ResponseEntity<List<RequestDtoResponse>> getAllRequests(@RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(requestService.getAllRequests(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDtoResponse> getRequestById(@PathVariable Long requestId,
                                                             @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(requestService.getRequestById(requestId, userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDtoResponse>> getAllWithPagination(@RequestParam(defaultValue = "0") Integer from,
                                                                         @RequestParam(defaultValue = "10") Integer size,
                                                                         @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(requestService.getAllByPages(from, size, userId));
    }
}
