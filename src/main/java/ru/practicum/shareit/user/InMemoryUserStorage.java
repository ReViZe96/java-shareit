package ru.practicum.shareit.user;


import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

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
        return Optional.ofNullable(users.put(id, newUser));
    }

    public Optional<User> updateUser(Long userId, User updateUser) {
        users.remove(userId);
        updateUser.setId(userId);
        return Optional.ofNullable(users.put(userId, updateUser));
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
