package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestCreateDto;
    private ItemRequestDto itemRequestGetDto;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository, itemRequestMapper);
        user = new User(1L, "userName", "user@test.com");
        itemRequest = new ItemRequest(1L, "газовая горелка", LocalDateTime.now(), user);
        item = new Item(1L, "газовая горелка", "подойдёт для всех видов работ", true, user, itemRequest);
        itemRequestCreateDto = new ItemRequestDto(null, "газовая горелка", null, null);
        itemRequestGetDto = new ItemRequestDto(1L, "газовая горелка", LocalDateTime.now(), null);
    }

    @Test
    void createItemRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestMapper.toItemRequest(any(), any()))
                .thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(any()))
                .thenReturn(itemRequestCreateDto);
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        ItemRequestDto itemRequestDtoCreated = itemRequestService.createItemRequest(user.getId(), itemRequestCreateDto);
        assertNotNull(itemRequestDtoCreated);
        verify(itemRequestRepository, times(1)).save(itemRequest);
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
        when(itemRequestMapper.toItemRequestDto(any()))
                .thenReturn(itemRequestGetDto);
        assertNotNull(itemRequestService.findItemRequestById(user.getId(), itemRequest.getId()));
        verify(itemRequestRepository, times(1)).findById(1L);
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
        when(itemRequestMapper.toItemRequestDto(any()))
                .thenReturn(itemRequestGetDto);
        itemRequestService.findAllUserItemsRequests(user.getId());
        verify(itemRequestRepository, times(1)).findAllByRequesterIdOrderByCreatedDesc(1L);
    }

    @Test
    void findAllUserItemRequestsUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findAllUserItemsRequests(42L));
    }

    @Test
    void findAllItemsRequestsTest() {
        List<ItemRequest> itemRequests = Collections.singletonList(itemRequest);
        Pageable pageable = PageRequest.of((0 / 10), 10, Sort.by(Sort.Direction.DESC, "created"));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(itemRequests);
        when(itemRepository.findAllByItemRequest_Id(anyLong()))
                .thenReturn(Collections.singletonList(item));
        when(itemRequestMapper.toItemRequestDto(any()))
                .thenReturn(itemRequestGetDto);
        itemRequestService.findAllItemsRequests(1L, 0, 10);
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdIsNotOrderByCreatedDesc(1L, pageable);
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

