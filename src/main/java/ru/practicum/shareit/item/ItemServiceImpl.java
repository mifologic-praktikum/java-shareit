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
    public List<Item> findAllItems(Long userId) {
        return itemStorage.findAllItems(userId);
    }

    @Override
    public Item findItemById(Long itemId) {
        return itemStorage.findItemById(itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemStorage.searchItems(text);
    }

    @Override
    public Item createItem(ItemDto itemDto, Long userId) {
        User owner = userStorage.findUserById(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return itemStorage.createItem(item);
    }

    @Override
    public Item updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Long ownerId = findItemById(itemId).getOwner().getId();
        if (!Objects.equals(ownerId, userId)) {
            throw new NotFoundException("This user can't update this item");
        }
        User owner = userStorage.findUserById(userId);
        Item item = ItemMapper.toItem(itemDto, owner);
        return itemStorage.updateItem(itemId, item);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemStorage.deleteItem(itemId);
    }
}
