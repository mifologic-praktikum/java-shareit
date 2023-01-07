package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> findAllItems(Long userId);

    ItemDto findItemById(Long userId, Long itemId);

    List<ItemDto> searchItems(String text);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    void deleteItem(Long itemId);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);


}
