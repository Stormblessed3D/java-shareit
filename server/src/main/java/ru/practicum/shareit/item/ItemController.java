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
import ru.practicum.shareit.comment.CommentDtoRequest;
import ru.practicum.shareit.comment.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.validator.OnCreate;
import ru.practicum.shareit.user.validator.OnUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.constant.ConstantKeeper.USER_REQUEST_HEADER;

@Controller
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDtoResponse>> getItems(@RequestParam(defaultValue = "0") @Min(value = 0L) Integer from,
                                                          @RequestParam(defaultValue = "10") @Positive @Max(value = 100) Integer size,
                                                          @RequestHeader(USER_REQUEST_HEADER) Long ownerId) {
        return ResponseEntity.ok(itemService.getItems(ownerId, from, size));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponse> getItemById(@PathVariable @Positive Long itemId,
                                                       @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(itemService.getItemById(itemId, userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDtoResponse>> search(@RequestParam String text,
                                                        @RequestParam(defaultValue = "0") @Min(value = 0L) Integer from,
                                                        @RequestParam(defaultValue = "10") @Positive @Max(value = 100) Integer size) {
        return ResponseEntity.ok(itemService.search(text, from, size));
    }

    @PostMapping
    public ResponseEntity<ItemDtoResponse> createItem(@Validated({OnCreate.class}) @RequestBody ItemDtoRequest itemDtoRequest,
                                                      @RequestHeader(USER_REQUEST_HEADER) Long ownerId) {
        return ResponseEntity.ok(itemService.createItem(itemDtoRequest, ownerId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDtoResponse> updateItem(@Validated({OnUpdate.class})  @RequestBody ItemDtoRequest itemDtoRequest,
                                                      @PathVariable @Positive Long itemId,
                                                      @RequestHeader(USER_REQUEST_HEADER) Long ownerId) {
        return ResponseEntity.ok(itemService.updateItem(itemDtoRequest, itemId, ownerId));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable @Positive Long itemId,
                                           @RequestHeader(USER_REQUEST_HEADER) Long ownerId) {
        itemService.deleteItem(itemId, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDtoResponse> createComment(@Valid @RequestBody CommentDtoRequest commentDtoRequest,
                                                            @PathVariable @Positive Long itemId,
                                                            @RequestHeader(USER_REQUEST_HEADER) Long userId) {
        return ResponseEntity.ok(itemService.createComment(commentDtoRequest, itemId, userId));
    }
}