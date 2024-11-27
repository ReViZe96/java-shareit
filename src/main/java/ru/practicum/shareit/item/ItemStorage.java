package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

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

    Optional<Item> editItem(Long itemId, Map<String, Object> updatedField, User owner);

    List<Item> findItems(String text);

}
