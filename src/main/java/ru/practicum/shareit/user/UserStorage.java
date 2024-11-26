package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAllUsers();

    Optional<User> getUserById(Long userId);

    Optional<User> addUser(User newUser);

    Optional<User> updateUser(Long userId, User updateUser);

    void deleteAllUsers();

    void deleteUserById(Long userId);

}
