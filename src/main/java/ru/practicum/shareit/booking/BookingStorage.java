package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface BookingStorage {

    List<Booking> getAllUserBookings(User requestedUser, String state);

    List<Booking> getAllItemBookings(Item item, String state);

    Optional<Booking> getBookingById(Long bookingId);

    Optional<Booking> addBooking(Booking newBooking);

    Optional<Booking> approveBooking(Booking booking, boolean approved);

}
