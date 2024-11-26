package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto addUser(UserDto newUser);

    UserDto updateUser(Long userId, UserDto updateUser);

    void deleteAllUsers();

    void deleteUserById(Long userId);

}
