package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    public List<ItemDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(name = "from", defaultValue = "0") int from,
                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Find all items");
        return itemService.findAllItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    ItemDto findItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Find item by id=" + itemId);
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping("/search")
    List<ItemDto> searchItem(@RequestParam("text") String text,
                             @RequestParam(name = "from", defaultValue = "0") int from,
                             @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Search items");
        return itemService.searchItems(text, from, size);
    }

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto itemDto) {
        log.info("Create item");
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@PathVariable Long itemId,
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
