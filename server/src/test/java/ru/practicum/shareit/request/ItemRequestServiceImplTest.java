package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ItemRequestServiceImplTest {

    private ItemRequestServiceImpl itemRequestService;

    private ItemRequestRepository itemRequestRepository;

    ItemRepository itemRepository;

    private UserRepository userRepository;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestCreateDto;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository);
        user = new User(1L, "userName", "user@test.com");
        itemRequest = new ItemRequest(1L, "газовая горелка", LocalDateTime.now(), user);
        item = new Item(1L, "газовая горелка", "подойдёт для всех видов работ", true, user, itemRequest);
        itemRequestCreateDto = new ItemRequestDto(null, "газовая горелка", null, null);
    }

    @Test
    void createItemRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        ItemRequestDto itemRequestDtoCreated = itemRequestService.createItemRequest(user.getId(), itemRequestCreateDto);
        assertNotNull(itemRequestDtoCreated);
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void createItemRequestUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(42L, itemRequestCreateDto));
    }

    @Test
    void findItemRequestByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        assertNotNull(itemRequestService.findItemRequestById(user.getId(), itemRequest.getId()));
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void findItemRequestByIdItemRequestNotFoundTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        assertThrows(NotFoundException.class, () -> itemRequestService.findItemRequestById(user.getId(), 42L));
    }

    @Test
    void findItemRequestByIdUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findItemRequestById(42L, 1L));
    }

    @Test
    void findAllUserItemsRequestsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        List<ItemRequest> requests = new ArrayList<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(requests);
        itemRequestService.findAllUserItemsRequests(user.getId());
        verify(itemRequestRepository, times(1)).findAllByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void findAllUserItemRequestsUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findAllUserItemsRequests(42L));
    }

    @Test
    void findAllItemsRequestsTest() {
        List<ItemRequest> itemRequests = Collections.singletonList(itemRequest);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(itemRequests);
        when(itemRepository.findAllByItemRequest_Id(anyLong()))
                .thenReturn(Collections.singletonList(item));
        itemRequestService.findAllItemsRequests(1L, 0, 10);
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdIsNotOrderByCreatedDesc(anyLong(), any());
    }

    @Test
    void findAllItemsRequestsUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findAllItemsRequests(42L, 0, 10));
    }

    @Test
    void findAllItemsRequestsNegativeTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> itemRequestService.findAllItemsRequests(user.getId(), -2, 10));
    }


}

