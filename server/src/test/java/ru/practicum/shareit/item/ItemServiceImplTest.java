package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

public class
ItemServiceImplTest {

    private ItemServiceImpl itemService;

    private ItemRepository itemRepository;

    private ItemRequestRepository itemRequestRepository;

    private UserRepository userRepository;

    private BookingRepository bookingRepository;

    private CommentRepository commentRepository;

    Item item;
    ItemDto itemDto;
    User user;
    ItemRequest itemRequest;
    Booking booking;
    CommentDto commentDto;
    Comment comment;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
        user = new User(1L, "userName", "user@test.com");
        itemRequest = new ItemRequest(1L, "газовая горелка", LocalDateTime.now(), user);
        item = new Item(1L, "газовая горелка", "Подойдёт для всех видов работ", true, user, itemRequest);
        itemDto = new ItemDto(1L, "газовая горелка", "подойдёт для всех видов работ", true, null, null, null, null);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED);
        commentDto = new CommentDto(1L, "test comment", item.getName(), LocalDateTime.now());
        comment = new Comment(1L, "test comment", item, user, LocalDateTime.now());
    }

    @Test
    void findAllItemsTest() {
        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAll(PageRequest.of((0 / 10), 10)))
                .thenReturn(itemPage);
        assertNotNull(itemService.findAllItems(1L, 0, 10));
        verify(itemRepository, times(1)).findAll(PageRequest.of((0 / 10), 10));
    }

    @Test
    void findItemByIdTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(Collections.singletonList(comment));
        assertNotNull(itemService.findItemById(1L, 1L));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void searchItemsTest() {
        final PageImpl<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAll(PageRequest.of((0 / 10), 10)))
                .thenReturn(itemPage);
        assertNotNull(itemService.searchItems("газовая горелка", 0, 10));
        verify(itemRepository, times(1)).findAll(PageRequest.of((0 / 10), 10));
    }

    @Test
    void searchItemsTextIsBlankTest() {
        assertEquals(Collections.emptyList(), itemService.searchItems("", 0, 10));
    }

    @Test
    void createItemTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);
        ItemDto itemDtoCreated = itemService.createItem(itemDto, user.getId());
        assertNotNull(itemDtoCreated);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void createItemWithRequestTest() {
        ItemDto itemWithRequestDto = new ItemDto(1L, "газовая горелка", "подойдёт для всех видов работ", true, null, null, null, 1L);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any()))
                .thenReturn(item);
        ItemDto itemDtoCreated = itemService.createItem(itemWithRequestDto, user.getId());
        assertNotNull(itemDtoCreated);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void addCommentTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        itemService.addComment(commentDto, 1L, 1L);
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addCommentUserHasNoBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThrows(UserHasNoBookings.class, () -> itemService.addComment(commentDto, 1L, 1L));
    }

    @Test
    void addCommentCommentDtoTextIsEmptyTest() {
        CommentDto commentDtoEmptyText = new CommentDto(1L, null, item.getName(), LocalDateTime.now());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        assertThrows(ValidationException.class, () -> itemService.addComment(commentDtoEmptyText, 1L, 1L));
    }

    @Test
    void addCommentCommentDtoTextIsBlankTest() {
        CommentDto commentDtoEmptyText = new CommentDto(1L, "", item.getName(), LocalDateTime.now());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        assertThrows(ValidationException.class, () -> itemService.addComment(commentDtoEmptyText, 1L, 1L));
    }

    @Test
    void addCommentItemNotFoundTest() {
        assertThrows(NotFoundException.class, () -> itemService.addComment(commentDto, 42L, 1L));
    }

    @Test
    void addCommentUserNotFoundTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.addComment(commentDto, 1L, 42L));
    }

    @Test
    void createItemUserNotFoundTest() {
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 42L));
    }

    @Test
    void itemNotFoundTest() {
        assertThrows(NotFoundException.class, () -> itemService.findItemById(1L, 42L));
    }

    @Test
    void findAllItemsBadRequest() {
        assertThrows(BadRequestException.class, () -> itemService.findAllItems(1L, -1, 10));
    }

    @Test
    void searchItemsBadRequestTest() {
        assertThrows(BadRequestException.class, () -> itemService.searchItems("газовая горелка", -2, 10));
    }

    @Test
    void updateItemDescriptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        ItemDto updateItem = new ItemDto(1L, null, "подойдёт для всех видов работ", null, null, null, null, null);
        ItemDto result = itemService.updateItem(1L, updateItem, 1L);
        assertEquals("подойдёт для всех видов работ", result.getDescription());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItemNameTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        ItemDto updateItem = new ItemDto(1L, "газовая горелка update", null, null, null, null, null, null);
        ItemDto result = itemService.updateItem(1L, updateItem, 1L);
        assertEquals("газовая горелка update", result.getName());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItemAvailableTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        ItemDto updateItem = new ItemDto(1L, null, null, true, null, null, null, null);
        ItemDto result = itemService.updateItem(1L, updateItem, 1L);
        assertEquals(true, result.getAvailable());
        verify(itemRepository, times(1)).save(any());
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

    @Test
    void updateItemItemNotFoundTest() {
        assertThrows(NotFoundException.class, () -> itemService.updateItem(42L, itemDto, 42L));
    }

    @Test
    void updateItemUserNotFoundTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDto, 42L));
    }
}
