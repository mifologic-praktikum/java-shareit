package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Find all items");
        return itemService.findAllItems(userId);
    }

    @GetMapping("/{itemId}")
    ItemDto findItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Find item by id=" + itemId);
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping("/search")
    List<ItemDto> searchItem(@RequestParam("text") String text) {
        log.info("Search items");
        return itemService.searchItems(text);
    }

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Create item");
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@Validated({Update.class}) @PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        log.info("Update item with id=" + itemId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    void deleteItem(@PathVariable Long itemId) {
        log.info("Delete item with id=" + itemId);
        itemService.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long itemId,
                             @RequestBody CommentDto commentDto) {
        log.info("Create comment");
        return itemService.addComment(commentDto, itemId, userId);
    }
}
