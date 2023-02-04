package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserHasNoBookings;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentMapper;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    @Mock
    CommentMapper commentMapper;

    Item item;
    ItemDto itemDto;
    User user;
    ItemRequest itemRequest;
    Booking booking;
    CommentDto commentDto;
    Comment comment;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository, itemMapper, commentMapper);
        user = new User(1L, "userName", "user@test.com");
        itemRequest = new ItemRequest(1L, "газовая горелка", LocalDateTime.now(), user);
        item = new Item(1L, "газовая горелка", "подойдёт для всех видов работ", true, user, itemRequest);
        itemDto = new ItemDto(1L, "газовая горелка", "подойдёт для всех видов работ", true, null, null, null, null);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED);
        commentDto = new CommentDto(1L, "test comment", item.getName(), LocalDateTime.now());
        comment = new Comment(1L, "test comment", item, user, LocalDateTime.now());
    }

    @Test
    void createItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemMapper.toItem(any(), any()))
                .thenReturn(item);
        when(itemMapper.toItemDto(any()))
                .thenReturn(itemDto);
        when(itemRepository.save(any()))
                .thenReturn(item);
        ItemDto itemDtoCreated = itemService.createItem(itemDto, user.getId());
        assertNotNull(itemDtoCreated);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void addCommentTest() {
        when(commentMapper.fromCommentDto(any()))
                .thenReturn(comment);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        itemService.addComment(commentDto, 1L, 1L);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void addCommentUserHasNoBookingTest() {
        when(commentMapper.fromCommentDto(any()))
                .thenReturn(comment);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThrows(UserHasNoBookings.class, () -> itemService.addComment(commentDto, 1L, 1L));
    }

    @Test
    void addCommentCommentDtoTextIsEmptyTest() {
        CommentDto commentDtoEmptyText = new CommentDto(1L, "", item.getName(), LocalDateTime.now());
        when(commentMapper.fromCommentDto(any()))
                .thenReturn(comment);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        assertThrows(ValidationException.class, () -> itemService.addComment(commentDtoEmptyText, 1L, 1L));
    }

    @Test
    void findItemByIdTest() {
        when(itemMapper.toItemDto(any()))
                .thenReturn(itemDto);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.singletonList(comment));
        itemService.findItemById(1L, 1L);
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void findAllItemsBadRequest() {
        assertThrows(BadRequestException.class, () -> itemService.findAllItems(1L, -1, 10));
    }

    @Test
    void findAllItemsTest() {
        when(itemMapper.toItemDto(any()))
                .thenReturn(itemDto);
        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(itemPage);
        itemService.findAllItems(1L, 0, 10);
        verify(itemRepository, times(1)).findAll(PageRequest.of(0, 10));
    }

    @Test
    void searchItemsTest() {
        when(itemMapper.toListItemDto(anyList()))
                .thenReturn(Collections.singletonList(itemDto));
        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(itemPage);
        assertNotNull(itemService.searchItems("газовая горелка", 0, 10));
        verify(itemRepository, times(1)).findAll(PageRequest.of(0, 10));
    }

    @Test
    void searchItemsBadRequestTest() {
        assertThrows(BadRequestException.class, () -> itemService.searchItems("газовая горелка", -2, 10));
    }

    @Test
    void updateItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        ItemDto updateItem = new ItemDto(1L, "газовая горелка update", "подойдёт для всех видов работ", true, null, null, null, null);
        when(itemMapper.toItemDto(any()))
                .thenReturn(updateItem);
        ItemDto result = itemService.updateItem(1L, itemDto, 1L);
        assertEquals("газовая горелка update", result.getName());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItemUserCannotUpdateItemTest() {
        User anotherUser = new User(2L, "userName", "user@test.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(anotherUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDto, anotherUser.getId()));
    }

}
