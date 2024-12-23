package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingFilter;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;


    /**
     * Получение списка всех бронирований текущего пользователя.
     * Бронирования возвращаются отсортированными по дате от более новых к более старым
     *
     * @param requestedUserId идентификатор пользователя, список бронирований которого необходимо получить
     * @param state           фильтрация возвращаемых бронирований по их состоянию
     *                        (по умолчанию - возвращаются все бронирования текущего пользователя)
     */
    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") long requestedUserId,
                                                     @RequestParam(name = "state", defaultValue = "all") String state) {
        BookingFilter existState = BookingFilter.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get booking with state {}, userId={}", existState, requestedUserId);
        return bookingClient.getAllUserBookings(requestedUserId, existState);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя-владельца.
     * Этот запрос имеет смысл для владельца хотя бы одной вещи.
     *
     * @param ownerId идентификатор пользователя-владельца, список бронирований для вещей которого необходимо получить
     * @param state   фильтрация возвращаемых бронирований по их состоянию (по умолчанию - возвращаются все бронирования
     *                вещей текущего пользователя-владельца
     */
    @GetMapping("/owner")
    public ResponseEntity<Object> getAllItemBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                     @RequestParam(name = "state", defaultValue = "all") String state) {
        BookingFilter existState = BookingFilter.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get booking with state {}, userId={}", existState, ownerId);
        return bookingClient.getAllItemBookings(ownerId, existState);
    }

    /**
     * Получение данных о конкретном бронировании (включая его статус).
     * Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование.
     *
     * @param userId    идентификатор пользователя, который создает запрос на получение информации о бронировании
     * @param bookingId идентификатор бронирования, информацию о котором небходимо получить
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    /**
     * Добавление нового запроса на бронирование.
     * Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
     * После создания запрос находится в статусе WAITING — «ожидает подтверждения».
     *
     * @param userId     идентификатор пользователя, который создает запрос на бронирование
     * @param newBooking DTO добавляемого запроса на бронирование
     * @return DTO добавленного запроса на бронирование
     */
    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody BookItemRequestDto newBooking) {
        log.info("Creating booking {}, userId={}", newBooking, userId);
        return bookingClient.addBooking(userId, newBooking);
    }

    /**
     * Подтверждение или отклонение запроса на бронирование.
     * Может быть выполнено только владельцем вещи.
     * После выполнения статус бронирования становится либо APPROVED, либо REJECTED.
     *
     * @param userId   идентификатор пользователя
     * @param approved если true - подтверждение бронирования, иначе - отклонение.
     * @return DTO запроса на бронирование с учетом его обновлённого состояния
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam boolean approved) {
        log.info("Approving booking {}, userId={}", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

}