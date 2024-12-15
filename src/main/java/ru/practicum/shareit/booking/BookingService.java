package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {

    List<BookingResponseDto> getAllUserBookings(Long requestedUserId, String state);

    List<BookingResponseDto> getAllItemBookings(Long ownerId, String state);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    BookingResponseDto addBooking(Long userId, BookingRequestDto newBooking);

    BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved);

    /**
     * Получить все бронирования указанной вещи, отфильтрованные по конктретному условию
     *
     * @param item   вещь, список бронирований которой нужно получить
     * @param filter условие фильтрации бронирований. Предусмотрен фильтр по статусу и времени бронирований
     */
    List<Booking> getItemAllBookings(Item item, BookingFilter filter);

    /**
     * Вернуть последнее бронирование вещи относительно текущей даты
     *
     * @param item вещь, для которой осуществляется поиск последнего бронирования
     */
    Booking findLastItemBooking(Item item);

    /**
     * Вернуть ближайшее следующее бронирование вещи относительно текущей даты
     *
     * @param item вещь, для которой осуществляется поиск ближайшего следующего бронирования
     */
    Booking findNextItemBooking(Item item);

}
