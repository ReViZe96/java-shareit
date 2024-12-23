package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingFilter;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long requestedUserId,
                                                                       @RequestParam(defaultValue = "ALL", required = false) BookingFilter existFilter) {
        return ResponseEntity.ok().body(bookingService.getAllUserBookings(requestedUserId, existFilter));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllItemBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                                       @RequestParam(defaultValue = "ALL", required = false) BookingFilter existFilter) {
        return ResponseEntity.ok().body(bookingService.getAllItemBookings(ownerId, existFilter));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @PathVariable Long bookingId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(userId, bookingId));
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestBody BookingRequestDto newBooking) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.addBooking(userId, newBooking));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @PathVariable Long bookingId,
                                                             @RequestParam boolean approved) {
        return ResponseEntity.ok().body(bookingService.approveBooking(userId, bookingId, approved));
    }

}
