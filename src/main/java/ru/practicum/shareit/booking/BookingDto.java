package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Data
public class BookingDto {

    private BookingStatus status;
    private Instant start;
    private Instant end;
    private User requestedUser;
    private Item requestedItem;

}
