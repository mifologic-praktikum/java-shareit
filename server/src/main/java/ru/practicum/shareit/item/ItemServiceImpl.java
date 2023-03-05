package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository, ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }


    @Override
    public List<ItemDto> findAllItems(Long userId, int from, int size) {
        List<ItemDto> userItems = new ArrayList<>();
        Pageable pageable = PageRequest.of((from / size), size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Item> itemList = itemRepository.findAll(pageable);
        for (Item item : itemList) {
            if (Objects.equals(item.getOwner().getId(), userId)) {
                ItemDto itemDto = setItemBookings(ItemMapper.toItemDto(item));
                userItems.add(itemDto);
            }
        }
        return userItems;
    }

    @Override
    public ItemDto findItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id= " + itemId + " not found")
        );
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            setItemBookings(itemDto);
        }
        List<ItemDto.CommentDto> commentsDto = toListItemCommentDto(commentRepository.findAllByItemId(itemId));
        itemDto.setComments(commentsDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of((from / size), size);
        return ItemMapper.toListItemDto(itemRepository.findAll(pageable).stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id= " + userId + " not found")
        );
        Item item = ItemMapper.toItem(itemDto, owner);
        item.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElse(new ItemRequest());
            item.setItemRequest(itemRequest);
            Item itemToRepositoryWithRequest = itemRepository.save(item);
            return ItemMapper.toItemDtoWithRequest(itemToRepositoryWithRequest);
        }
        Item itemToRepository = itemRepository.save(item);
        return ItemMapper.toItemDto(itemToRepository);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item itemInStorage = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id= " + itemDto.getId() + " not found")
        );
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id= " + userId + " not found")
        );
        if (itemInStorage.getOwner().getId().equals(owner.getId())) {
            if (itemDto.getName() != null) {
                itemInStorage.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                itemInStorage.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                itemInStorage.setAvailable(itemDto.getAvailable());
            }
            itemRepository.save(itemInStorage);
            return ItemMapper.toItemDto(itemInStorage);
        } else {
            throw new NotFoundException("This user can't update this item");
        }
    }

    @Transactional
    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id= " + itemId + " not found")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id= " + userId + " not found")
        );
        List<Booking> bookings = bookingRepository.findAllByItemAndBookerIdAndStatusAndEndBefore(
                item, userId, BookingStatus.APPROVED, LocalDateTime.now()
        );
        if (bookings.isEmpty()) {
            throw new UserHasNoBookings("User should have at least one active booking");
        }
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Text can't be empty");
        }
        Comment comment = CommentMapper.fromCommentDto(commentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(user);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private ItemDto setItemBookings(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        bookingRepository.findLastItemBooking(itemDto.getId(), now)
                .ifPresent(booking -> itemDto.setLastBooking(new ItemDto.ItemBookingDto(booking.getId(), booking.getBooker().getId())));
        bookingRepository.findNextItemBooking(itemDto.getId(), now)
                .ifPresent(booking -> itemDto.setNextBooking(new ItemDto.ItemBookingDto(booking.getId(), booking.getBooker().getId())));
        return itemDto;
    }

    private static List<ItemDto.CommentDto> toListItemCommentDto(List<Comment> comments) {
        List<ItemDto.CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(new ItemDto.CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(),
                    comment.getCreated()));
        }
        return commentDtos;
    }
}
