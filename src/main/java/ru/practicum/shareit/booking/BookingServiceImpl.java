package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.errors.ForbidenForUserOperationException;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ParameterNotValidException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemService itemService;
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;


    @Override
    public List<BookingResponseDto> getAllUserBookings(Long requestedUserId, String state) {
        User existRequestedUser = isUserExist(requestedUserId);
        BookingFilter existFilter = isBookingFilterExist(state);
        log.info("Запрос всех бронирований пользователя {}", existRequestedUser.getName());
        return bookingStorage.getAllUserBookings(existRequestedUser, existFilter)
                .stream()
                .map(b -> getBookingResponse(b, false))
                .toList();
    }

    @Override
    public List<BookingResponseDto> getAllItemBookings(Long ownerId, String state) {
        isUserExist(ownerId);
        BookingFilter existFilter = isBookingFilterExist(state);
        log.info("Запрос всех вещей, которыми владеет пользователь с id = {}", ownerId);
        List<Item> ownersItems = itemService.getAllItems(ownerId).stream().map(itemMapper::itemDtoToItem).toList();
        if (ownersItems.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + ownerId + " не является владельцем ни для одной вещи");
        } else {
            List<Booking> result = new ArrayList<>();
            for (Item ownerItem : ownersItems) {
                log.info("Запрос бронирований вещи {}", ownerItem.getName());
                result.addAll(bookingStorage.getAllItemBookings(ownerItem, existFilter));
            }
            return result.stream().map(b -> getBookingResponse(b, true)).toList();
        }
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        isUserExist(userId);
        log.info("Запрос бронирования с id = {}", bookingId);
        Optional<Booking> booking = bookingStorage.getBookingById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Бронирование с id = " + bookingId + " не найдено");
        } else {
            if (!userId.equals(booking.get().getRequestedUser().getId())
                    && !userId.equals(booking.get().getRequestedItem().getOwner().getId())) {
                throw new ForbidenForUserOperationException("Пользователь с id = " + userId + " не может просматривать " +
                        "информацию о бронировании с id = " + bookingId + " т.к. он не бронировал эту вещь и " +
                        "не является владельцем бронирумой вещи");
            } else {
                return booking.map(b -> getBookingResponse(b, false)).get();
            }
        }
    }

    @Override
    public BookingResponseDto addBooking(Long userId, BookingRequestDto newBooking) {
        log.info("Получен запрос на добавление бронирования вещи с id = {} пользователем с id = {}",
                newBooking.getItemId(), userId);
        Optional<User> requestedUser = userStorage.getUserById(userId);
        if (requestedUser.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + userId + ", не найден");
        } else {
            log.info("Пользователь с id = {} существует", userId);
            Item requestedItem = isBookingRequestValid(newBooking);
            Booking booking = bookingMapper.bookingRequestDtoToBooking(newBooking, requestedItem);
            booking.setRequestedUser(requestedUser.get());
            booking.setStatus(BookingStatus.WAITING);
            return bookingStorage.addBooking(booking).map(b -> getBookingResponse(b, false)).get();
        }
    }

    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved) {
        log.info("Получен запрос на подтверждение/отклонение бронирования с id = {}", bookingId);
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
                        .map(b -> getBookingResponse(b, false)).get();
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

    private BookingFilter isBookingFilterExist(String state) {
        BookingFilter stateFilter = BookingFilter.from(state);
        if (stateFilter == null) {
            throw new ParameterNotValidException("Передан некорректный фильтр бронирования");
        } else {
            log.info("Фильтр запрашиваемых бронирований {} валиден", state);
            return stateFilter;
        }
    }

    private Item isBookingRequestValid(BookingRequestDto newBooking) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = newBooking.getStart();
        LocalDateTime end = newBooking.getEnd();
        if (start == null || end == null) {
            throw new ValidationException("У бронирования должны быть дата начала и дата конца");
        }
        if (start.equals(end)) {
            throw new ValidationException("У бронирования дата начала и дата конца не должны совпадать");
        }
        if (now.isAfter(start) || now.isAfter(end)) {
            throw new ValidationException("У бронирования дата начала и дата конца не должны быть в прошлом");
        }
        Optional<Item> requestedItem = itemStorage.getItemById(newBooking.getItemId());
        if (requestedItem.isEmpty()) {
            throw new NotFoundException("Бронируемая вещь с id = " + newBooking.getItemId() + " не найдена");
        }
        if (!requestedItem.get().getAvailable()) {
            throw new ValidationException("На данный момент вещь недоступна для броинрования");
        }
        return requestedItem.get();
    }

    /**
     * Сформировать DTO бронирования, возвращаемое в ответ на запрос.
     * Поля, содержащие информацию о последнем и ближайшем следующем бронировании вещи
     * заполняются только в ответ на запрос владельца вещи
     *
     * @param booking         обрабатываемое бронирование
     * @param isOwnerResponse является ли пользователь, запрашивающий информацию о бронировании, владельцем бронируемой вещи
     */
    private BookingResponseDto getBookingResponse(Booking booking, boolean isOwnerResponse) {
        Item requestedItem = booking.getRequestedItem();
        return bookingMapper.bookingToBookingResponseDto(booking,
                userMapper.userToUserDto(booking.getRequestedUser()),
                itemMapper.itemToItemDto(requestedItem, itemService.findLastItemBooking(requestedItem),
                        itemService.findNextItemBooking(requestedItem),
                        itemService.findAllItemComments(requestedItem),
                        isOwnerResponse));
    }

}
