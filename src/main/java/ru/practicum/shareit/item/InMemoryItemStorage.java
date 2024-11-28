package ru.practicum.shareit.item;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {

    private HashMap<Long, Item> items = new HashMap<>();

    public Collection<Item> getAllItems(Long ownerId) {
        return items.values().stream().filter(i -> ownerId.equals(i.getOwner().getId())).toList();
    }

    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public Optional<Item> addItem(Item item) {
        long id = getNextId();
        item.setId(id);
        items.put(id, item);
        return Optional.of(item);
    }

    public Optional<Item> editItem(Long itemId, Map<String, Object> updatedField, User owner) {
        Item updatingItem = items.get(itemId);
        Set<Map.Entry<String, Object>> entry = updatedField.entrySet();
        for (Map.Entry<String, Object> field : entry) {
            if (field.getKey().equals(NAME)) {
                updatingItem.setName(field.getValue().toString());
            }
            if (field.getKey().equals(DESCRIPTION)) {
                updatingItem.setDescription(field.getValue().toString());
            }
            if (field.getKey().equals(AVAILABLE)) {
                updatingItem.setAvailable((Boolean) field.getValue());
            }
        }
        updatingItem.setOwner(owner);
        items.put(itemId, updatingItem);
        return Optional.of(updatingItem);
    }

    public List<Item> findItems(String text) {
        Set<Item> withContainedName = items.values()
                .stream().filter(i -> StringUtils.containsIgnoreCase(i.getName(), text))
                .filter(Item::getAvailable).collect(Collectors.toSet());
        Set<Item> withContainedDescription = items.values()
                .stream().filter(i -> StringUtils.containsIgnoreCase(i.getDescription(), text))
                .filter(Item::getAvailable).collect(Collectors.toSet());
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
