package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.NotOwnerTryEditException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private ItemStorage itemStorage;
    private UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }


    public List<ItemDto> getAllItems(Long ownerId) {
        log.info("Запрос всех вещей пользователя с id = {}", ownerId);
        return itemStorage.getAllItems(ownerId).stream().map(ItemMapper::mapToItemDto).toList();
    }

    public ItemDto getItemById(Long itemId) {
        log.info("Запрос информации о вещи с id = {}", itemId);
        return itemStorage.getItemById(itemId).map(ItemMapper::mapToItemDto).get();
    }

    public ItemDto addItem(ItemDto newItem, Long ownerId) {
        log.info("Получен запрос на добавление вещи под названием {}", newItem.getName());
        checkItemName(newItem);
        checkItemDescription(newItem);
        checkItemAvailable(newItem);
        isOwnerExist(ownerId);
        log.info("Добавляемая вещь валидна");
        User owner = userStorage.getUserById(ownerId).get();
        Item item = ItemMapper.mapToItem(newItem, owner);
        return itemStorage.addItem(item).map(ItemMapper::mapToItemDto).get();
    }

    public ItemDto editItem(Long itemId, ItemDto editedItem, Long ownerId) {
        log.info("Получен запрос на редактирование информации о вещи с id = {}", itemId);
        Map<String, Object> updatedField = new HashMap<>();
        if (editedItem.getName() != null) {
            checkItemName(editedItem);
            log.info("Новое значение поля name валидно");
            updatedField.put(ItemStorage.NAME, editedItem.getName());
        }
        if (editedItem.getDescription() != null) {
            checkItemDescription(editedItem);
            log.info("Новое значение поля description валидно");
            updatedField.put(ItemStorage.DESCRIPTION, editedItem.getDescription());
        }
        if (editedItem.getAvailable() != null) {
            checkItemAvailable(editedItem);
            log.info("Новое значение поля available валидно");
            updatedField.put(ItemStorage.AVAILABLE, editedItem.getAvailable());
        }
        User owner = isOwnerExist(ownerId).get();
        log.info("Владелец вещи существует в системе");
        Optional<Item> existItem = itemStorage.getItemById(itemId);
        if (existItem.isPresent()) {
            log.info("Обновляемая вещь существует в системе");
            if (existItem.get().getOwner().equals(owner)) {
                return itemStorage.editItem(itemId, updatedField, owner).map(ItemMapper::mapToItemDto).get();
            } else {
                throw new NotOwnerTryEditException("Пользователю " + owner.getName() +
                        " запрещено редактировать информацию о вещи " +
                        existItem.get().getName() + " т.к. владельцем вещи является другой пользователь: " +
                        existItem.get().getOwner().getName());
            }
        } else {
            throw new NotFoundException("Обновляемая вещь с id = " + itemId + " не найдена в системе");
        }
    }

    public List<ItemDto> findItems(String text) {
        if (text.isEmpty()) {
            log.info("Поисковой запрос содержит пустую строку");
            return Collections.emptyList();
        } else {
            log.info("Получен поисковой запрос: {}", text);
            return itemStorage.findItems(text).stream().map(ItemMapper::mapToItemDto).toList();
        }
    }


    private void checkItemName(ItemDto item) {
        String name = item.getName();
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new ValidationException("У вещи должно быть название");
        }
    }

    private void checkItemDescription(ItemDto item) {
        String description = item.getDescription();
        if (description == null || description.isEmpty() || description.isBlank()) {
            throw new ValidationException("У вещи должно быть описание");
        }
    }

    private void checkItemAvailable(ItemDto item) {
        Boolean available = item.getAvailable();
        if (available == null) {
            throw new ValidationException("У вещи должен быть указан статус её доступности для бронирования");
        }
    }

    private Optional<User> isOwnerExist(Long ownerId) {
        Optional<User> owner = userStorage.getUserById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + ownerId + ", указанный в качестве владельца не найден");
        } else {
            return owner;
        }
    }

}
