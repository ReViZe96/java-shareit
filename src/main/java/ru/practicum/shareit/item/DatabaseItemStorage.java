package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component("databaseItemStorage")
@Primary
@RequiredArgsConstructor
public class DatabaseItemStorage implements ItemStorage {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public Collection<Item> getAllItems(Long ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            return Collections.emptyList();
        } else {
            return itemRepository.findByOwner(owner.get());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Item> getItemById(Long itemId) {
        return itemRepository.findById(itemId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<Item> addItem(Item item) {
        return Optional.of(itemRepository.save(item));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void editItem(Long itemId, Map<String, Object> updatedField, User owner) {
        Set<Map.Entry<String, Object>> entry = updatedField.entrySet();
        for (Map.Entry<String, Object> field : entry) {
            if (field.getKey().equals(NAME)) {
                itemRepository.setNameById(itemId, field.getValue().toString());
            }
            if (field.getKey().equals(DESCRIPTION)) {
                itemRepository.setDescriptionById(itemId, field.getValue().toString());
            }
            if (field.getKey().equals(AVAILABLE)) {
                itemRepository.setAvailableById(itemId, (Boolean) field.getValue());
            }
        }
        itemRepository.setOwnerById(itemId, owner.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findItems(String text) {
        Set<Item> withContainedName = new HashSet<>(itemRepository.findAllByNameContainingIgnoreCase(text));
        Set<Item> withContainedDescription = new HashSet<>(itemRepository.findAllByDescriptionContainingIgnoreCase(text));
        withContainedName.addAll(withContainedDescription);
        return withContainedName.stream().toList();
    }

}
