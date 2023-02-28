package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto findItemRequestById(Long userId, Long requestId);

    List<ItemRequestDto> findAllUserItemsRequests(Long userId);

    List<ItemRequestDto> findAllItemsRequests(Long userId, int from, int size);
}
