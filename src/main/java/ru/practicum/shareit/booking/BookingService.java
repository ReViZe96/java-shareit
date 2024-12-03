package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    List<BookingDto> getAllUserBookings(Long requestedUserId, String state);

    List<BookingDto> getAllItemBookings(Long ownerId, String state);

    BookingDto getBookingById(Long userId, Long bookingId);

    BookingDto addBooking(Long userId, BookingDto newBooking);

    BookingDto approveBooking(Long userId, Long bookingId, boolean approved);

}
