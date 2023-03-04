package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		checkBookingDates(requestDto);
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.createBooking(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
												@RequestParam("approved") Boolean approved) {
		log.info("Update booking with id={}", bookingId);
		return bookingClient.updateBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
												  @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.findBookingById(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> findBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
	@RequestParam(name = "state", defaultValue = "ALL") BookingState state,
	@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
	@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.findBookingByOwner(userId, state, from, size);
	}

	@GetMapping
	public ResponseEntity<Object> findBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
													@RequestParam(name = "state", defaultValue = "ALL") BookingState state,
													@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
													@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.findBookingByUser(userId, state, from, size);
	}

	private void checkBookingDates(BookItemRequestDto requestDto) {
		if (requestDto.getStart().isBefore(LocalDateTime.now())) {
			throw new BadRequestException("Start date can't be before in the past");
		}
		if (requestDto.getEnd().isBefore(requestDto.getStart())) {
			throw new BadRequestException("End date can't be before start date");
		}
	}

}
