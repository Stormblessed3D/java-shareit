package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.validator.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok(itemService.getItems(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable @Positive Long itemId) {
        return ResponseEntity.ok(ItemMapper.toItemDto(itemService.getItemById(itemId)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        return ResponseEntity.ok(itemService.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }

    @PostMapping
    @Validated({OnCreate.class})
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item item = ItemMapper.toItem(itemDto, null, ownerId, true);
        return ResponseEntity.ok(ItemMapper.toItemDto(itemService.createItem(item)));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@Valid @RequestBody ItemDto itemDto,
                                              @PathVariable @Positive Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item item = ItemMapper.toItem(itemDto, itemId, ownerId, false);
        return ResponseEntity.ok(ItemMapper.toItemDto(itemService.updateItem(item)));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable @Positive Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        itemService.deleteItem(itemId, ownerId);
    }
}
