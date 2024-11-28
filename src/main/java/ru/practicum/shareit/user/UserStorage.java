package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    String EMAIL = "email";
    String NAME = "name";

    Collection<User> getAllUsers();

    Optional<User> getUserById(Long userId);

    Optional<User> addUser(User newUser);

    Optional<User> updateUser(Long userId, Map<String, Object> updatedField);

    void deleteAllUsers();

    void deleteUserById(Long userId);

}
