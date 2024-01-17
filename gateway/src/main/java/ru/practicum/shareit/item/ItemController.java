package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.user.validator.OnCreate;
import ru.practicum.shareit.user.validator.OnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.constant.ConstantKeeper.USER_REQUEST_HEADER;

@Controller
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestParam(defaultValue = "0") @Min(value = 0L) Integer from,
                                           @RequestParam(defaultValue = "10") @Positive @Max(value = 100) Integer size,
                                           @RequestHeader(USER_REQUEST_HEADER) Long ownerId) {
        return itemClient.getItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable @Positive Long itemId,
                                          @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestParam(defaultValue = "0") @Min(value = 0L) Integer from,
                                         @RequestParam(defaultValue = "10") @Positive @Max(value = 100) Integer size,
                                         @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return itemClient.search(text, from, size, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated({OnCreate.class}) @RequestBody ItemDtoRequest itemDtoRequest,
                                             @RequestHeader(USER_REQUEST_HEADER) Long ownerId) {
        return itemClient.createItem(ownerId, itemDtoRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated({OnUpdate.class})  @RequestBody ItemDtoRequest itemDtoRequest,
                                             @PathVariable @Positive Long itemId,
                                             @RequestHeader(USER_REQUEST_HEADER) Long ownerId) {
        return itemClient.updateItem(ownerId, itemId, itemDtoRequest);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable @Positive Long itemId,
                                           @RequestHeader(USER_REQUEST_HEADER) Long ownerId) {
        itemClient.deleteItem(itemId, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDtoRequest commentDtoRequest,
                                                @PathVariable @Positive Long itemId,
                                                @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return itemClient.createComment(userId, itemId, commentDtoRequest);
    }
}
