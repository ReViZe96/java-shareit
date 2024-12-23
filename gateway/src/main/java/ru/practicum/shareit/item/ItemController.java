package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated

public class ItemController {

    private final ItemClient itemClient;


    /**
     * Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой из них
     *
     * @param ownerId идентификатор пользователя — владельца вещи.
     */
    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Get all items of user with id = {}", ownerId);
        return itemClient.getAllItems(ownerId);
    }

    /**
     * Просмотр информации о конкретной вещи. Информацию о вещи может просмотреть любой пользователь.
     *
     * @param itemId идентификатор вещи
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId) {
        log.info("Get item with id = {}", itemId);
        return itemClient.getItemById(itemId);
    }

    /**
     * Добавление новой вещи.
     * Предусмотрена обработка запроса на добавление вещи, запрошенной другим пользователем
     * (DTO такой вещи содержит значение для поля requestId).
     *
     * @param newItem DTO добавляемой вещи
     * @param ownerId идентификатор пользователя, который добавляет вещь. Именно этот пользователь — владелец вещи.
     * @return DTO добавленной вещи
     */
    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemDto newItem,
                                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Create new item");
        return itemClient.addItem(newItem, ownerId);
    }

    /**
     * Редактирование иформации о вещи. Изменить можно название, описание и статус доступа к аренде.
     * Редактировать вещь может только её владелец.
     *
     * @param itemId     идентификатор редактируемой вещи
     * @param editedItem DTO, содержащее вносимые в информацию о вещи изменения
     * @param ownerId    идентификатор пользователя, который редактирует вещь
     * @return DTO измененной вещи
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@PathVariable Long itemId,
                                           @RequestBody @Valid ItemDto editedItem,
                                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Update item with id = {}", itemId);
        return itemClient.editItem(itemId, editedItem, ownerId);
    }

    /**
     * Поиск вещей. Возвращаются только доступные для аренды на данный момент вещи (Item.available = true).
     *
     * @param text текст для поиска вещей (поисковой запрос)
     * @return Список вещей, содержащих текст из поискового запроса в названии или описании.
     */
    @GetMapping("/search")
    public ResponseEntity<Object> findItems(@RequestParam("text") String text) {
        log.info("Find items by text: {}", text);
        return itemClient.findItems(text);
    }

    /**
     * Добавление отзыва о вещи, бывшей в бронировании.
     * Отзыв может оставлять только пользователь, который действительно брал вещь в аренду.
     *
     * @param itemId     идентификатор вещи, о которой будет оставлен отзыв
     * @param newComment DTO добавляемого отзыва
     * @param authorId   идентификатор пользователя-автора создаваемого отзыва
     * @return DTO добавленного отзыва
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @RequestBody @Valid CommentDto newComment,
                                             @RequestHeader("X-Sharer-User-Id") Long authorId) {
        log.info("Add comment on item with id = {}", itemId);
        return itemClient.addComment(itemId, newComment, authorId);
    }

}
