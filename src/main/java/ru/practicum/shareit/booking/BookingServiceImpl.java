package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.errors.ForbidenForUserOperationException;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ParameterNotValidException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemService itemService;
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;


    @Override
    public List<BookingDto> getAllUserBookings(Long requestedUserId, String state) {
        User existRequestedUser = isUserExist(requestedUserId);
        BookingFilter existState = isBookingStateExist(state);
        log.info("Запрос всех бронирований пользователя {}", existRequestedUser.getName());
        return bookingStorage.getAllUserBookings(existRequestedUser, existState.name())
                .stream()
                .map(bookingMapper::bookingToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getAllItemBookings(Long ownerId, String state) {
        isUserExist(ownerId);
        BookingFilter existState = isBookingStateExist(state);
        log.info("Запрос всех вещей, которыми владеет пользователь с id = {}", ownerId);
        List<Item> ownersItems = itemService.getAllItems(ownerId).stream().map(itemMapper::itemDtoToItem).toList();
        if (ownersItems.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + ownerId + " не является владельцем ни для одной вещи");
        } else {
            List<Booking> result = new ArrayList<>();
            for (Item ownerItem : ownersItems) {
                log.info("Запрос бронирований вещи {}", ownerItem.getName());
                result.addAll(bookingStorage.getAllItemBookings(ownerItem, existState.name()));
            }
            return result.stream().map(bookingMapper::bookingToBookingDto).toList();
        }
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        isUserExist(userId);
        log.info("Запрос бронирования с id = {}", bookingId);
        Optional<Booking> booking = bookingStorage.getBookingById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Бронирование с id = " + bookingId + " не найдено");
        } else {
            if (!userId.equals(booking.get().getRequestedUser().getId())
                    || !userId.equals(booking.get().getRequestedItem().getOwner().getId())) {
                throw new ForbidenForUserOperationException("Пользователь с id = " + userId + " не может просматривать " +
                        "информацию о бронировании с id = " + bookingId + " т.к. он не бронировал эту вещь и " +
                        "не является владельцем бронирумой вещи");
            } else {
                return booking.map(bookingMapper::bookingToBookingDto).get();
            }
        }
    }

    @Override
    public BookingDto addBooking(Long userId, BookingDto newBooking) {
        Booking booking = bookingMapper.bookingDtoToBooking(newBooking);
        log.info("Получен запрос на добавление бронирования вещи {} пользователем с id = {}",
                booking.getRequestedItem().getName(), userId);
        Optional<User> requestedUser = userStorage.getUserById(userId);
        if (requestedUser.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + userId + ", не найден");
        } else {
            log.info("Пользователь с id = {} существует", userId);
            booking.setRequestedUser(requestedUser.get());
            booking.setStatus(BookingStatus.WAITING);
            return bookingStorage.addBooking(booking).map(bookingMapper::bookingToBookingDto).get();
        }
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, boolean approved) {
        log.info("Получен запрос на подтверждение/отклонение бронирования с id = {}", bookingId);
        isUserExist(userId);
        Optional<Booking> booking = bookingStorage.getBookingById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Бронирование с id = " + bookingId + " не найдено");
        } else {
            if (!userId.equals(booking.get().getRequestedItem().getOwner().getId())) {
                throw new ForbidenForUserOperationException("Пользователь с id = " + userId + " не может одобрить/отклонить " +
                        "запрос на бронирование вещи " + booking.get().getRequestedItem().getName() +
                        " т.к. не является владельцем данной вещи");
            } else {
                return bookingStorage.approveBooking(booking.get(), approved)
                        .map(bookingMapper::bookingToBookingDto).get();
            }
        }
    }


    private User isUserExist(Long userId) {
        Optional<User> owner = userStorage.getUserById(userId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + userId + ", не найден");
        } else {
            log.info("Пользователь с id = {} существует", userId);
            return owner.get();
        }
    }

    private BookingFilter isBookingStateExist(String state) {
        BookingFilter stateFilter = BookingFilter.from(state);
        if (stateFilter == null) {
            throw new ParameterNotValidException("Передан некорректный статус бронирования");
        } else {
            log.info("Статус запрашиваемых бронирований {} валиден", state);
            return stateFilter;
        }
    }

}
