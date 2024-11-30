package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.SameEmailException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;


    public Collection<UserDto> getAllUsers() {
        log.info("Запрос списка всех пользователей");
        return userStorage.getAllUsers().stream().map(userMapper::userToUserDto).toList();
    }

    public UserDto getUserById(Long userId) {
        log.info("Запрос информации о пользователе с id = {}", userId);
        return userStorage.getUserById(userId).map(userMapper::userToUserDto).get();
    }

    public UserDto addUser(UserDto newUser) {
        log.info("Получен запрос на добавление пользователя с именем {}", newUser.getName());
        checkUserEmail(newUser);
        log.info("Добавляемый пользователь валиден");
        User user = userMapper.userDtoToUser(newUser);
        return userStorage.addUser(user).map(userMapper::userToUserDto).get();
    }

    public UserDto updateUser(Long userId, UserDto updateUser) {
        log.info("Получен запрос на редактирование информации о пользователе с id = {}", userId);
        Map<String, Object> updatedField = new HashMap<>();
        if (updateUser.getEmail() != null) {
            checkUserEmail(updateUser);
            log.info("Новое значение электронной почты валидно");
            updatedField.put(UserStorage.EMAIL, updateUser.getEmail());
        }
        if (updateUser.getName() != null) {
            updatedField.put(UserStorage.NAME, updateUser.getName());
        }
        Optional<User> existUser = userStorage.getUserById(userId);
        if (existUser.isPresent()) {
            log.info("Обновляемый пользователь существует в системе");
            userStorage.updateUser(userId, updatedField);
            return userStorage.getUserById(userId).map(userMapper::userToUserDto).get();
        } else {
            throw new NotFoundException("Обновляемый пользователь с id = " + userId + " не найден в системе");
        }
    }

    public void deleteAllUsers() {
        log.info("Получен запрос на удаление всех пользователей");
        userStorage.deleteAllUsers();
    }

    public void deleteUserById(Long userId) {
        log.info("Получен запрос на удаление пользователя с id {}", userId);
        userStorage.deleteUserById(userId);
    }

    private void checkUserEmail(UserDto user) {
        String email = user.getEmail();
        if (email == null || email.isEmpty() || email.isBlank()) {
            throw new ValidationException("У пользователя должен быть заполнен адрес электронной почты");
        }
        if (!email.contains("@") || email.contains(" ")) {
            throw new ValidationException("У пользователя " + user.getName() + " указан некорректный адрес электронной почты");
        }
        List<String> existEmails = userStorage.getAllUsers().stream().map(User::getEmail).toList();
        if (existEmails.contains(email)) {
            throw new SameEmailException("Адрес электронной почты: " + email + " уже занят другим пользователем");
        }
    }

}
