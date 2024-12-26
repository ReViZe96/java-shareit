package ru.practicum.shareit.other;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingFilter;

public class BookingFilterTest {

    @Test
    public void checkBookingFilterTest() {
        BookingFilter all = BookingFilter.from("all");
        BookingFilter current = BookingFilter.from("current");
        BookingFilter past = BookingFilter.from("past");
        BookingFilter future = BookingFilter.from("future");
        BookingFilter waiting = BookingFilter.from("waiting");
        BookingFilter rejected = BookingFilter.from("rejected");
        Assertions.assertEquals(BookingFilter.ALL, all);
        Assertions.assertEquals(BookingFilter.CURRENT, current);
        Assertions.assertEquals(BookingFilter.PAST, past);
        Assertions.assertEquals(BookingFilter.FUTURE, future);
        Assertions.assertEquals(BookingFilter.WAITING, waiting);
        Assertions.assertEquals(BookingFilter.REJECTED, rejected);
    }
}
