package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDto;

@Component
public class BookingMapper {

    public Booking bookingRequestDtoToBooking(BookingRequestDto bookingDto, Item requestedItem) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setRequestedItem(requestedItem);
        return booking;
    }

    public BookingResponseDto bookingToBookingResponseDto(Booking booking, UserDto booker, ItemDto requestedItem) {
        BookingResponseDto response = new BookingResponseDto();
        response.setId(booking.getId());
        response.setStatus(booking.getStatus());
        response.setStart(booking.getStart());
        response.setEnd(booking.getEnd());
        response.setBooker(booker);
        response.setItem(requestedItem);
        return response;
    }

}
