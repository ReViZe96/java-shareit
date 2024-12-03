package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component("inMemoryBookingStorage")
//@Primary
public class InMemoryBookingStorage implements BookingStorage {

    private HashMap<Long, Booking> bookings = new HashMap<>();


    @Override
    public List<Booking> getAllUserBookings(User requestedUser, String state) {
        return bookings.values()
                .stream().
                filter(b -> requestedUser.equals(b.getRequestedUser()))
                .filter(b -> state.equals(b.getStatus().name()))
                .toList();
    }

    @Override
    public List<Booking> getAllItemBookings(Item item, String state) {
        return bookings.values()
                .stream().
                filter(b -> item.equals(b.getRequestedItem()))
                .filter(b -> state.equals(b.getStatus().name()))
                .toList();
    }

    @Override
    public Optional<Booking> getBookingById(Long bookingId) {
        return Optional.of(bookings.get(bookingId));
    }

    @Override
    public Optional<Booking> addBooking(Booking newBooking) {
        long id = getNextId();
        newBooking.setId(id);
        bookings.put(id, newBooking);
        return Optional.of(newBooking);
    }

    @Override
    public Optional<Booking> approveBooking(Booking booking, boolean approved) {
        Booking approvingBooking = bookings.get(booking.getId());
        if (approved) {
            approvingBooking.setStatus(BookingStatus.APPROVED);
        } else {
            approvingBooking.setStatus(BookingStatus.REJECTED);
        }
        bookings.put(approvingBooking.getId(), approvingBooking);
        return Optional.of(approvingBooking);
    }


    private long getNextId() {
        long currentMaxId = bookings.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
