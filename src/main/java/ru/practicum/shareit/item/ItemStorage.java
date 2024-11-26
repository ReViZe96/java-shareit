package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Collection<Item> getAllItems(Long ownerId);

    Optional<Item> getItemById(Long itemId);

    Optional<Item> addItem(Item item);

    Optional<Item> editItem(Long itemId, Item item);

    List<Item> findItems(String text);

}
