package ru.practicum.shareit.other;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

public class BookingTest {

    @Test
    public void chekBookingCompare() {
        Booking first = new Booking();
        first.setStart(LocalDateTime.of(2020,05,10,15,1));
        first.setEnd(LocalDateTime.of(2021,05,10,15,1));

        Booking second = new Booking();
        second.setStart(LocalDateTime.of(2022,05,10,15,1));
        second.setEnd(LocalDateTime.of(2023,05,10,15,1));

        Assertions.assertEquals(-1, first.compareTo(second));
    }

}
