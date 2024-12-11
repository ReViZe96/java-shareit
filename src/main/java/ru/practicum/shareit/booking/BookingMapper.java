package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final ItemService itemService;

    public Booking bookingRequestDtoToBooking(BookingRequestDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        ItemDto requestedItem = itemService.getItemById(bookingDto.getItemId());
        booking.setRequestedItem(itemMapper.itemDtoToItem(requestedItem));
        return booking;
    }

    public BookingResponseDto bookingToBookingResponseDto(Booking booking) {
        BookingResponseDto response = new BookingResponseDto();
        response.setId(booking.getId());
        response.setStatus(booking.getStatus());
        response.setStart(booking.getStart());
        response.setEnd(booking.getEnd());
        User requestedUser = booking.getRequestedUser();
        response.setBooker(userMapper.userToUserDto(requestedUser));
        Item requestedItem = booking.getRequestedItem();
        response.setItem(itemMapper.itemToItemDto(requestedItem, false));
        return response;
    }

}
