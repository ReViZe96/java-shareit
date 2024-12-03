package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Component("databaseBookingStorage")
@Primary
@RequiredArgsConstructor
public class DatabaseBookingStorage implements BookingStorage {

    private final BookingRepository bookingRepository;


    public List<Booking> getAllUserBookings(User requestedUser, String state) {
        return bookingRepository.findByRequestedUserAndStatus(requestedUser, state);
    }

    public List<Booking> getAllItemBookings(Item item, String state) {
        return bookingRepository.findByRequestedItemAndStatus(item, state);
    }

    public Optional<Booking> getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    public Optional<Booking> addBooking(Booking newBooking) {
        return Optional.of(bookingRepository.save(newBooking));
    }

    public Optional<Booking> approveBooking(Booking booking, boolean approved) {
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return Optional.of(bookingRepository.save(booking));
    }

}
