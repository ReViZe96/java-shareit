package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;


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
    public ResponseEntity<Object> getAllAnotherUserRequests(@RequestHeader("X-Sharer-User-Id") Long requestedUserId) {
        log.info("Get all another user's requests by user with id = {}", requestedUserId);
        return requestClient.getAllAnotherUserRequests(requestedUserId);
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
    public ResponseEntity<Object> getOnlyThisUserRequests(@RequestHeader("X-Sharer-User-Id") Long requestedUserId) {
        log.info("Get all requests of user with id ={}", requestedUserId);
        return requestClient.getOnlyThisUserRequests(requestedUserId);
    }

    /**
     * Получение данных об одном конкретном запросе вместе с данными об ответах на него.
     * Посмотреть данные об отдельном запросе может любой пользователь.
     *
     * @param requestId идентификатор запроса, информацию о котором требуется вернуть.
     * @return DTO требуемого запроса.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId) {
        log.info("Get request with id = {}", requestId);
        return requestClient.getRequestById(requestId);
    }

    /**
     * Добавление нового запроса вещи.
     *
     * @param itemRequest     текст запроса, в котором пользователь описывает, какая именно вещь ему нужна.
     * @param requestedUserId идентификатор пользователя, который запрашивает вещь.
     * @return DTO запроса вещи
     */
    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestBody @Valid ItemRequestDto itemRequest,
                                             @RequestHeader("X-Sharer-User-Id") Long requestedUserId) {
        log.info("Create new request");
        return requestClient.addRequest(itemRequest, requestedUserId);
    }

}
