package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {

    private HashMap<Long, Item> items = new HashMap<>();


    public Collection<Item> getAllItems(Long ownerId) {
        return items.values();
    }

    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public Optional<Item> addItem(Item item) {
        long id = getNextId();
        item.setId(id);
        return Optional.ofNullable(items.put(id, item));
    }

    public Optional<Item> editItem(Long itemId, Item item) {
        items.remove(itemId);
        item.setId(itemId);
        return Optional.ofNullable(items.put(itemId, item));
    }

    public List<Item> findItems(String text) {
        Set<Item> withContainedName = items.values()
                .stream().filter(i -> i.getName().contains(text)).filter(Item::getAvailable).collect(Collectors.toSet());
        Set<Item> withContainedDescription = items.values()
                .stream().filter(i -> i.getDescription().contains(text)).filter(Item::getAvailable).collect(Collectors.toSet());
        withContainedName.addAll(withContainedDescription);
        return withContainedName.stream().toList();
    }


    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
