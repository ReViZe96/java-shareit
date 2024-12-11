package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component("databaseItemStorage")
@Primary
@RequiredArgsConstructor
public class DatabaseItemStorage implements ItemStorage {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
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
        Item item = itemRepository.findById(itemId).get();
        for (Map.Entry<String, Object> field : entry) {
            if (field.getKey().equals(NAME)) {
                item.setName(field.getValue().toString());
            }
            if (field.getKey().equals(DESCRIPTION)) {
                item.setDescription(field.getValue().toString());
            }
            if (field.getKey().equals(AVAILABLE)) {
                item.setAvailable((Boolean) field.getValue());
            }
        }
        itemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findItems(String text) {
        Set<Item> withContainedName = new HashSet<>(itemRepository.findAllByNameContainingIgnoreCase(text));
        Set<Item> withContainedDescription = new HashSet<>(itemRepository.findAllByDescriptionContainingIgnoreCase(text));
        withContainedName.addAll(withContainedDescription);
        return withContainedName.stream().toList();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<Comment> addComment(Comment comment) {
        return Optional.of(commentRepository.save(comment));
    }

}
