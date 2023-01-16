package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookigService bookingService;


    public BookingController(BookigService bookigService) {
        this.bookingService = bookigService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody NewBookingDto newBookingDto) {
        log.info("Create booking");
        return bookingService.createBooking(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                    @RequestParam("approved") Boolean approved) {
        log.info("Update booking");
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Find booking by id=" + userId);
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "state", defaultValue = "ALL")
                                              BookingState state,
                                              @RequestParam(name = "from", defaultValue = "0") int from,
                                              @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Find bookings by state=" + state);
        return bookingService.findBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(
                                                        value = "state", defaultValue = "ALL", required = false) BookingState state,
                                                @RequestParam(name = "from", defaultValue = "0") int from,
                                                @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Find owner's booking by state=" + state);
        return bookingService.findBookingsByOwner(userId, state, from, size);
    }
}
