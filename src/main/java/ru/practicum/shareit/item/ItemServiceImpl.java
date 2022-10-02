package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Objects;

@Service
public class ItemServiceImpl implements ItemService {

    private final InMemoryItemStorage itemStorage;
    private final InMemoryUserStorage userStorage;

    public ItemServiceImpl(InMemoryItemStorage itemStorage, InMemoryUserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }


    @Override
    public List<ItemDto> findAllItems(Long userId) {
        return  ItemMapper.toListItemDto(itemStorage.findAllItems(userId));
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.findItemById(itemId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return ItemMapper.toListItemDto(itemStorage.searchItems(text));
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = userStorage.findUserById(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item itemById = itemStorage.findItemById(itemId);
        if (!Objects.equals(itemById.getOwner().getId(), userId)) {
            throw new NotFoundException("This user can't update this item");
        }
        User owner = userStorage.findUserById(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, item));
    }

    @Override
    public void deleteItem(Long itemId) {
        itemStorage.deleteItem(itemId);
    }
}
