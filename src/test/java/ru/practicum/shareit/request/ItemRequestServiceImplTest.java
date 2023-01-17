package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    private ItemRequest request;
    private ItemRequestDto itemRequestCreateDto;
    private ItemRequestDto itemRequestGetDto;
    private User user;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository, itemRequestMapper);
        user = new User(1L, "userName", "user@test.com");
        request = new ItemRequest(1L, "газовая горелка", LocalDateTime.now(), user);
        itemRequestCreateDto = new ItemRequestDto(null, "газовая горелка", null, null);
        itemRequestGetDto = new ItemRequestDto(1L, "газовая горелка", LocalDateTime.now(), null);
    }

    @Test
    void createItemRequestTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestMapper.toItemRequest(any(), any()))
                .thenReturn(request);
        when(itemRequestRepository.save(any()))
                .thenReturn(request);

        itemRequestService.createItemRequest(user.getId(), itemRequestCreateDto);
        verify(itemRequestRepository, times(1)).save(request);
    }

    @Test
    void createRequestUserNotFoundTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("User not found"));
        assertThrows(RuntimeException.class, () -> itemRequestService.createItemRequest(user.getId(), itemRequestCreateDto));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findItemRequestByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRequestMapper.toItemRequestDto(any()))
                .thenReturn(itemRequestGetDto);
        itemRequestService.findItemRequestById(user.getId(), request.getId());
        verify(itemRequestRepository, times(1)).findById(1L);
    }

    @Test
    void findAllUserItemsRequestsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        List<ItemRequest> requests = new ArrayList<>(Collections.singletonList(request));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(requests);
        when(itemRequestMapper.toItemRequestDto(any()))
                .thenReturn(itemRequestGetDto);
        itemRequestService.findAllUserItemsRequests(user.getId());
        verify(itemRequestRepository, times(1)).findAllByRequesterIdOrderByCreatedDesc(1L);
    }

    @Test
    void findAllItemsRequestsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        List<ItemRequest> requests = new ArrayList<>(Collections.singletonList(request));
        when(itemRequestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(anyLong(), PageRequest.of(anyInt(), 10)))
                .thenReturn(requests);
        itemRequestService.findAllItemsRequests(user.getId(), 0, 10);
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdIsNotOrderByCreatedDesc(1L, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created")));
    }


}

