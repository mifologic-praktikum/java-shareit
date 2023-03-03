package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

      public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requester = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id= " + userId + " not found")
        );
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        itemRequest.setRequester(requester);
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto findItemRequestById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id= " + userId + " not found")
        );
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Item with id=" + requestId + " not found"));
        List<ItemRequestDto.ItemDto> itemDto = addItems(requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemDto);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> findAllUserItemsRequests(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id= " + userId + " not found")
        );
        Pageable pageable = PageRequest.of((from / size), size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId, pageable);
        List<ItemRequestDto> itemRequestsWithItems = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemRequestDto.ItemDto> itemDto = addItems(itemRequest.getId());
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            itemRequestDto.setItems(itemDto);
            itemRequestsWithItems.add(itemRequestDto);
        }
        return itemRequestsWithItems;
    }

    @Override
    public List<ItemRequestDto> findAllItemsRequests(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id= " + userId + " not found")
        );
        Pageable pageable = PageRequest.of((from / size), size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(userId, pageable);
        List<ItemRequestDto> itemRequestsWithItems = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemRequestDto.ItemDto> itemDto = addItems(itemRequest.getId());
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            itemRequestDto.setItems(itemDto);
            itemRequestsWithItems.add(itemRequestDto);
        }
        return itemRequestsWithItems;
    }

    private List<ItemRequestDto.ItemDto> addItems(Long requestId) {
        List<Item> items = itemRepository.findAllByItemRequest_Id(requestId);
        return items.stream().map(
                        item -> new ItemRequestDto.ItemDto(
                                item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                                item.getItemRequest().getId()))
                .collect(Collectors.toList());
    }
}
