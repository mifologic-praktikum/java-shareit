package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookigService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    @Override
    public BookingDto createBooking(Long userId, NewBookingDto newBookingDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found")
        );
        Item item = itemRepository.findById(newBookingDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Item with id=" + newBookingDto.getItemId() + " not found")
        );
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("User can't book his own item");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("This item is unavailable for booking");
        }
        Booking booking = BookingMapper.toBooking(newBookingDto, item, user);
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BadRequestException("End date can't be before start date");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start date can't be before in the past");
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }


    @Transactional
    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id=" + bookingId + " not found")
        );
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Booking for this user not found");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("Can't change this booking");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id=" + bookingId + " not found")
        );
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("User with id=" + userId + " can't see information about this booking");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findBookingsByOwner(Long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found"));
        List<Booking> bookingList = new ArrayList<>();
        if (from < 0) {
            throw new BadRequestException("Can't be negative");
        }
        Pageable pageable = PageRequest.of((from / size), size);
        switch (state) {
            case ALL:
                bookingList.addAll(bookingRepository.findAllByItem_Owner_IdOrderByIdDesc(userId, pageable));
                break;
            case CURRENT:
                bookingList.addAll(bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageable));
                break;
            case FUTURE:
                bookingList.addAll(bookingRepository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), pageable));
                break;
            case PAST:
                bookingList.addAll(bookingRepository.findAllByItem_Owner_IdAndEndBefore(userId, LocalDateTime.now(), pageable));
                break;
            case WAITING:
                bookingList.addAll(bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable));
                break;
            case REJECTED:
                bookingList.addAll(bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable));
                break;
        }
        return BookingMapper.toListBookingDto(bookingList);
    }

    @Override
    public List<BookingDto> findBookingsByUser(Long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found"));
        List<Booking> bookingList = new ArrayList<>();
        if (from < 0) {
            throw new BadRequestException("Can't be negative");
        }
        Pageable pageable = PageRequest.of((from / size), size);
        switch (state) {
            case ALL:
                bookingList.addAll(bookingRepository.findAllByBooker_IdOrderByIdDesc(userId, pageable));
                break;
            case CURRENT:
                bookingList.addAll(bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageable));
                break;
            case FUTURE:
                bookingList.addAll(bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), pageable));
                break;
            case PAST:
                bookingList.addAll(bookingRepository.findAllByBooker_IdAndEndBefore(userId, LocalDateTime.now(), pageable));
                break;
            case WAITING:
                bookingList.addAll(bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable));
                break;
            case REJECTED:
                bookingList.addAll(bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable));
                break;
        }
        return BookingMapper.toListBookingDto(bookingList);
    }
}
