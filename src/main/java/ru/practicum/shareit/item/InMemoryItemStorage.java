package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemStorage {

    private static Long itemId = 0L;
    private final Map<Long, Item> items = new TreeMap<>();

    private Long generateItemId() {
        return ++itemId;
    }
    public List<Item> findAllItems(Long userId) {
        List<Item> userItems = new ArrayList<>();
        for(Item item : items.values()) {
            if(item.getOwner().getId() == userId) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    public Item findItemById(Long itemId) {
        return items.get(itemId);
    }

    public List<Item> searchItems(String text) {
        List<Item> foundItems = new ArrayList<>();
        if(text.isBlank()) {
            return Collections.emptyList();
        }
        for(Item item : items.values()) {
            if(item.getName().toLowerCase().contains(text.toLowerCase()) & item.getAvailable() ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())  & item.getAvailable()) {
                foundItems.add(item);
            }
        }
        return foundItems;
    }

    public Item createItem(Item item) {
        item.setId(generateItemId());
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Long itemId, Item item) {
        Item itemInMemory = findItemById(itemId);
        if(item.getName() != null) {
            itemInMemory.setName(item.getName());
        }
        if(item.getDescription() != null) {
            itemInMemory.setDescription(item.getDescription());
        }
        if(item.getAvailable() != null) {
            itemInMemory.setAvailable(item.getAvailable());
        }
        return itemInMemory;
    }

    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }
}
