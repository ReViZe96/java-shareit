package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.DeleteDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto addUser(UserDto newUser);

    UserDto updateUser(Long userId, UserDto updateUser);

    DeleteDto deleteAllUsers();

    void deleteUserById(Long userId);

}
