package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    List<BookingResponseDto> getAllUserBookings(Long requestedUserId, String state);

    List<BookingResponseDto> getAllItemBookings(Long ownerId, String state);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    BookingResponseDto addBooking(Long userId, BookingRequestDto newBooking);

    BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved);

}
