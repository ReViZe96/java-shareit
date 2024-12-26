package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRequestedUser(User requestedUser);

    List<Booking> findByRequestedItem(Item requestedItem);

    List<Booking> findByRequestedUserAndStatus(User requestedUser, BookingStatus status);

    List<Booking> findByRequestedItemAndStatus(Item requestedItem, BookingStatus status);

    Optional<Booking> findById(Long id);

}
