package ru.practicum.shareit.user;


import org.springframework.stereotype.Component;

import java.util.*;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private HashMap<Long, User> users = new HashMap<>();


    public Collection<User> getAllUsers() {
        return users.values();
    }

    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Optional<User> addUser(User newUser) {
        long id = getNextId();
        newUser.setId(id);
        users.put(id, newUser);
        return Optional.of(newUser);
    }

    public Optional<User> updateUser(Long userId, Map<String, Object> updatedField) {
        User updatingUser = users.get(userId);
        Set<Map.Entry<String, Object>> entry = updatedField.entrySet();
        for (Map.Entry<String, Object> field : entry) {
            if (field.getKey().equals(EMAIL)) {
                updatingUser.setEmail(field.getValue().toString());
            }
            if (field.getKey().equals(NAME)) {
                updatingUser.setName(field.getValue().toString());
            }
        }
        users.put(userId, updatingUser);
        return Optional.of(updatingUser);
    }

    public void deleteAllUsers() {
        users.clear();
    }

    public void deleteUserById(Long userId) {
        users.remove(userId);
    }


    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
