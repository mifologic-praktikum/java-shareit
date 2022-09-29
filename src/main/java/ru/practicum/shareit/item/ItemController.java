package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllItems(userId);
    }

    @GetMapping("/{itemId}")
    Item findItemById(@PathVariable Long itemId) {
        return itemService.findItemById(itemId);
    }

    @GetMapping("/search")
    List<Item> searchItem(@RequestParam("text") String text) {
        return itemService.searchItems(text);
    }

    @PostMapping
    Item createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    Item updateItem(@Validated({Update.class}) @PathVariable Long itemId,
                    @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }
}
