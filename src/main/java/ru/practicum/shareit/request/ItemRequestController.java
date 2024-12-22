package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;


    /**
     * Получение текущим пользователем списка запросов, созданных другими пользователями.
     * С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
     * на которые они могли бы ответить.
     * Запросы сортируются по дате создания от более новых к более старым.
     *
     * @param requestedUserId идентификатор текущего пользователя.
     * @return список DTO запросов всех других пользователей.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseDto>> getAllAnotherUserRequests(@RequestHeader("X-Sharer-User-Id") Long requestedUserId) {
        return ResponseEntity.ok(itemRequestService.getAllAnotherUserRequests(requestedUserId));
    }

    /**
     * Получение текущим пользователем списка своих запросов вместе с данными об ответах на них.
     * Для каждого запроса должны быть указаны описание, дата и время создания,
     * а также список ответов в формате: id вещи, название, id владельца.
     * В дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой из них.
     * Запросы должны возвращаться отсортированными от более новых к более старым.
     *
     * @param requestedUserId идентификатор текущего пользователя.
     * @return список DTO запросов текущего пользователя.
     */
    @GetMapping
    public ResponseEntity<List<ItemRequestResponseDto>> getOnlyThisUserRequests(@RequestHeader("X-Sharer-User-Id") Long requestedUserId) {
        return ResponseEntity.ok(itemRequestService.getOnlyThisUserRequests(requestedUserId));
    }

    /**
     * Получение данных об одном конкретном запросе вместе с данными об ответах на него.
     * Посмотреть данные об отдельном запросе может любой пользователь.
     *
     * @param requestId идентификатор запроса, информацию о котором требуется вернуть.
     * @return DTO требуемого запроса.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseDto> getRequestById(@PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.getRequestById(requestId));
    }


    /**
     * Добавление нового запроса вещи.
     *
     * @param itemRequest     текст запроса, в котором пользователь описывает, какая именно вещь ему нужна.
     * @param requestedUserId идентификатор пользователя, который запрашивает вещь.
     * @return DTO запроса вещи
     */
    @PostMapping
    public ResponseEntity<ItemRequestResponseDto> addRequest(@RequestBody ItemRequestDto itemRequest,
                                                             @RequestHeader("X-Sharer-User-Id") Long requestedUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemRequestService.addRequest(itemRequest, requestedUserId));
    }

}
