package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilter;
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


    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllUserBookings(User user, BookingFilter filter) {
        List<Booking> userBookings = bookingRepository.findByRequestedUser(user);
        switch (filter) {
            case ALL:
                return userBookings;
            case CURRENT, PAST, FUTURE:
                filterBookingsByTimeFilter(userBookings, filter);
            default:
                BookingStatus state = BookingStatus.valueOf(filter.name());
                return bookingRepository.findByRequestedUserAndStatus(user, state);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllItemBookings(Item item, BookingFilter filter) {
        List<Booking> itemBookings = bookingRepository.findByRequestedItem(item);
        switch (filter) {
            case ALL:
                return itemBookings;
            case CURRENT, PAST, FUTURE:
                filterBookingsByTimeFilter(itemBookings, filter);
            default:
                BookingStatus state = BookingStatus.valueOf(filter.name());
                return bookingRepository.findByRequestedItemAndStatus(item, state);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Booking> getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<Booking> addBooking(Booking newBooking) {
        return Optional.of(bookingRepository.save(newBooking));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<Booking> approveBooking(Booking booking, boolean approved) {
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return Optional.of(bookingRepository.save(booking));
    }

}
