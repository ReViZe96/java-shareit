package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.NotFoundException;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream().map(UserMapper::mapToUserDto).toList();
    }

    public UserDto getUserById(Long userId) {
        return userStorage.getUserById(userId).map(UserMapper::mapToUserDto).get();
    }

    public UserDto addUser(UserDto newUser) {
        User user = UserMapper.mapToUser(newUser);
        return userStorage.addUser(user).map(UserMapper::mapToUserDto).get();
    }

    public UserDto updateUser(Long userId, UserDto updateUser) {
        Optional<User> existUser = userStorage.getUserById(userId);
        if (existUser.isPresent()) {
            User user = UserMapper.mapToUser(updateUser);
            return userStorage.updateUser(userId, user).map(UserMapper::mapToUserDto).get();
        } else {
            throw new NotFoundException("Обновляемый пользователь с id = " + userId + " не найден в системе");
        }
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    public void deleteUserById(Long userId) {
        userStorage.deleteUserById(userId);
    }

}
