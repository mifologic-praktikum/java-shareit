package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    ItemMapper itemMapper;

    Item item;
    ItemDto itemDto;
    User user;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository, itemMapper);
        user = new User(1L, "userName", "user@test.com");
        itemRequest = new ItemRequest(1L, "газовая горелка", LocalDateTime.now(), user);
        item = new Item(1L, "газоавя горелка", "подойдёт для всех видов работ", true, user, itemRequest);
        itemDto = new ItemDto(1L, "газоавя горелка", "подойдёт для всех видов работ", true, null, null, null, null);
    }

    @Test
    void createItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemMapper.toItem(any(), any()))
                .thenReturn(item);
        when(itemRepository.save(any()))
                .thenReturn(item);
        itemService.createItem(itemDto, user.getId());
        verify(itemRepository, times(1)).save(item);

    }
}
