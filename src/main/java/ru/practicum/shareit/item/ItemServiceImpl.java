package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.NotOwnerTryEditException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.Optional;

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
        return itemStorage.getAllItems(ownerId).stream().map(ItemMapper::mapToItemDto).toList();
    }

    public ItemDto getItemById(Long itemId) {
        return itemStorage.getItemById(itemId).map(ItemMapper::mapToItemDto).get();
    }

    public ItemDto addItem(ItemDto newItem, Long ownerId) {
        User owner = userStorage.getUserById(ownerId).get();
        Item item = ItemMapper.mapToItem(newItem, owner);
        return itemStorage.addItem(item).map(ItemMapper::mapToItemDto).get();
    }

    public ItemDto editItem(Long itemId, ItemDto editedItem, Long ownerId) {
        Optional<Item> existItem = itemStorage.getItemById(itemId);
        if (existItem.isPresent()) {
            User owner = userStorage.getUserById(ownerId).get();
            if (existItem.get().getOwner().equals(owner)) {
                Item item = ItemMapper.mapToItem(editedItem, owner);
                return itemStorage.editItem(itemId, item).map(ItemMapper::mapToItemDto).get();
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
        return itemStorage.findItems(text).stream().map(ItemMapper::mapToItemDto).toList();
    }

}
