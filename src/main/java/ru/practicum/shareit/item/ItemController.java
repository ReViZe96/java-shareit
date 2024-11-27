package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    /**
     * Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой из них
     *
     * @param ownerId идентификатор пользователя — владельца вещи.
     */
    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllItems(ownerId);
    }

    /**
     * Просмотр информации о конкретной вещи. Информацию о вещи может просмотреть любой пользователь.
     *
     * @param itemId идентификатор вещи
     */
    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    /**
     * Добавление новой вещи.
     *
     * @param newItem DTO добавляемой вещи
     * @param ownerId идентификатор пользователя, который добавляет вещь. Именно этот пользователь — владелец вещи.
     * @return DTO добавленной вещи
     */
    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto newItem,
                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.addItem(newItem, ownerId);
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
    public ItemDto editItem(@PathVariable Long itemId,
                            @RequestBody ItemDto editedItem,
                            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.editItem(itemId, editedItem, ownerId);
    }

    /**
     * Поиск вещей. Возвращаются только доступные для аренды на данный момент вещи (Item.available = true).
     *
     * @param text текст для поиска вещей (поисковой запрос)
     * @return Список вещей, содержащих текст из поискового запроса в названии или описании.
     */
    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam("text") String text) {
        return itemService.findItems(text);
    }

}
