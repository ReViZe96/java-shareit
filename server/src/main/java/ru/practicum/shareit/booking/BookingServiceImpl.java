package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.errors.ForbidenForUserOperationException;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ParameterNotValidException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;


    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllUserBookings(Long requestedUserId, BookingFilter existFilter) {
        User existRequestedUser = isUserExist(requestedUserId);
        log.info("Запрос всех бронирований пользователя {}", existRequestedUser.getName());

        List<Booking> userBookings = bookingRepository.findByRequestedUser(existRequestedUser);
        List<Booking> result;
        switch (existFilter) {
            case ALL:
                result = userBookings;
                break;
            case CURRENT, PAST, FUTURE:
                result = filterBookingsByTimeFilter(userBookings, existFilter);
                break;
            default:
                BookingStatus status = BookingStatus.valueOf(existFilter.name());
                result = bookingRepository.findByRequestedUserAndStatus(existRequestedUser, status);
        }
        return result.stream()
                .map(b -> getBookingResponse(b, false))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllItemBookings(Long ownerId, BookingFilter existFilter) {
        User owner = isUserExist(ownerId);
        log.info("Запрос всех вещей, которыми владеет пользователь с id = {}", ownerId);
        List<Item> ownersItems = itemRepository.findByOwner(owner);
        if (ownersItems.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + ownerId + " не является владельцем ни для одной вещи");
        } else {
            List<Booking> result = new ArrayList<>();
            for (Item ownerItem : ownersItems) {
                log.info("Запрос бронирований вещи {}", ownerItem.getName());
                result.addAll(getItemAllBookings(ownerItem, existFilter));
            }
            return result.stream().map(b -> getBookingResponse(b, true)).toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        isUserExist(userId);
        log.info("Запрос бронирования с id = {}", bookingId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
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
    @Transactional
    public BookingResponseDto addBooking(Long userId, BookingRequestDto newBooking) {
        log.info("Получен запрос на добавление бронирования вещи с id = {} пользователем с id = {}",
                newBooking.getItemId(), userId);
        Optional<User> requestedUser = userRepository.findById(userId);
        if (requestedUser.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + userId + ", не найден");
        } else {
            log.info("Пользователь с id = {} существует", userId);
            Item requestedItem = isBookingRequestValid(newBooking);
            Booking booking = bookingMapper.bookingRequestDtoToBooking(newBooking, requestedItem);
            booking.setRequestedUser(requestedUser.get());
            booking.setStatus(BookingStatus.WAITING);
            return Optional.of(bookingRepository.save(booking)).map(b -> getBookingResponse(b, false)).get();
        }
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved) {
        log.info("Получен запрос на подтверждение/отклонение бронирования с id = {}", bookingId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new NotFoundException("Бронирование с id = " + bookingId + " не найдено");
        } else {
            if (!userId.equals(booking.get().getRequestedItem().getOwner().getId())) {
                throw new ForbidenForUserOperationException("Пользователь с id = " + userId + " не может одобрить/отклонить " +
                        "запрос на бронирование вещи " + booking.get().getRequestedItem().getName() +
                        " т.к. не является владельцем данной вещи");
            } else {
                if (approved) {
                    booking.get().setStatus(BookingStatus.APPROVED);
                } else {
                    booking.get().setStatus(BookingStatus.REJECTED);
                }
                return Optional.of(bookingRepository.save(booking.get()))
                        .map(b -> getBookingResponse(b, false)).get();
            }
        }
    }

    @Override
    public List<Booking> getItemAllBookings(Item item, BookingFilter filter) {
        List<Booking> itemBookings = bookingRepository.findByRequestedItem(item);
        switch (filter) {
            case ALL:
                return itemBookings;
            case CURRENT, PAST, FUTURE:
                filterBookingsByTimeFilter(itemBookings, filter);
            default:
                BookingStatus state = BookingStatus.valueOf(filter.name());
                return bookingRepository.findByRequestedItemAndStatus(item, state);
        }
    }

    @Override
    public Booking findLastItemBooking(Item item) {
        List<Booking> itemBookings = bookingRepository.findByRequestedItem(item);
        LocalDateTime now = LocalDateTime.now();
        return itemBookings.stream()
                .filter(b -> now.isAfter(b.getStart()))
                .min(Collections.reverseOrder())
                .orElse(null);
    }

    @Override
    public Booking findNextItemBooking(Item item) {
        List<Booking> itemBookings = bookingRepository.findByRequestedItem(item);
        LocalDateTime now = LocalDateTime.now();
        return itemBookings.stream()
                .filter(b -> now.isBefore(b.getStart()))
                .sorted()
                .findFirst()
                .orElse(null);
    }


    private User isUserExist(Long userId) {
        Optional<User> owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + userId + ", не найден");
        } else {
            log.info("Пользователь с id = {} существует", userId);
            return owner.get();
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
        Optional<Item> requestedItem = itemRepository.findById(newBooking.getItemId());
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
                itemMapper.itemToItemDto(requestedItem, findLastItemBooking(requestedItem),
                        findNextItemBooking(requestedItem),
                        commentRepository.findByCommentedItem(requestedItem).stream().map(commentMapper::commentToCommentDto).toList(),
                        isOwnerResponse));
    }

    /**
     * Отфильтровать список бронирований относительно текущего момента времени
     *
     * @param bookingList список бронирований, который нужно отфильтровать
     * @param timeFilter  каким образом выполнить фильтрацию
     */
    private List<Booking> filterBookingsByTimeFilter(List<Booking> bookingList, BookingFilter timeFilter) {
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
