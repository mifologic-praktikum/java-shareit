package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> findAllItems(Long userId);

    Item findItemById(Long itemId);

    List<Item> searchItems(String text);

    Item createItem(ItemDto itemDto, Long userId);

    Item updateItem(Long itemId, ItemDto itemDto, Long userId);

    void deleteItem(Long itemId);


}
