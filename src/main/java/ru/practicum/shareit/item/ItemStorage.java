package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ItemStorage {

    String NAME = "name";
    String DESCRIPTION = "description";
    String AVAILABLE = "available";

    Collection<Item> getAllItems(Long ownerId);

    Optional<Item> getItemById(Long itemId);

    Optional<Item> addItem(Item item);

    void editItem(Long itemId, Map<String, Object> updatedField, User owner);

    List<Item> findItems(String text);

}
