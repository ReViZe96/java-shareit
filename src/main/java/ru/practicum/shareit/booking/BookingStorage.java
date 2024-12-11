package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.errors.ParameterNotValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage {

    List<Booking> getAllUserBookings(User user, BookingFilter filter);

    List<Booking> getAllItemBookings(Item item, BookingFilter filter);

    Optional<Booking> getBookingById(Long bookingId);

    Optional<Booking> addBooking(Booking newBooking);

    Optional<Booking> approveBooking(Booking booking, boolean approved);

    default List<Booking> filterBookingsByTimeFilter(List<Booking> bookingList, BookingFilter timeFilter) {
        LocalDateTime now = LocalDateTime.now();
        switch (timeFilter) {
            case CURRENT:
                return bookingList.stream()
                        .filter(b -> now.isAfter(b.getStart()))
                        .filter(b -> now.isBefore(b.getEnd()))
                        .toList();
            case PAST:
                return bookingList.stream()
                        .filter(b -> now.isAfter(b.getStart()))
                        .filter(b -> now.isAfter(b.getEnd()))
                        .toList();
            case FUTURE:
                return bookingList.stream()
                        .filter(b -> now.isBefore(b.getStart()))
                        .filter(b -> now.isBefore(b.getEnd()))
                        .toList();
            default:
                throw new ParameterNotValidException("Передан некорректный фильтр бронирования");
        }
    }

}
