package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    /**
     * Получение списка всех бронирований текущего пользователя.
     * Бронирования возвращаются отсортированными по дате от более новых к более старым
     *
     * @param requestedUserId идентификатор пользователя, список бронирований которого необходимо получить
     * @param state           фильтрация возвращаемых бронирований по их состоянию
     *                        (по умолчанию - возвращаются все бронирования текущего пользователя)
     */
    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long requestedUserId,
                                                               @RequestParam(defaultValue = "ALL", required = false) String state) {
        return ResponseEntity.ok().body(bookingService.getAllUserBookings(requestedUserId, state));
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
    public ResponseEntity<List<BookingDto>> getAllItemBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                               @RequestParam(defaultValue = "ALL", required = false) String state) {
        return ResponseEntity.ok().body(bookingService.getAllItemBookings(ownerId, state));
    }

    /**
     * Получение данных о конкретном бронировании (включая его статус).
     * Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование.
     *
     * @param userId    идентификатор пользователя, который создает запрос на получение информации о бронировании
     * @param bookingId идентификатор бронирования, информацию о котором небходимо получить
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long bookingId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(userId, bookingId));
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
    public ResponseEntity<BookingDto> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 BookingDto newBooking) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.addBooking(userId, newBooking));
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
    @PatchMapping("{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam boolean approved) {
        return ResponseEntity.ok().body(bookingService.approveBooking(userId, bookingId, approved));
    }

}
