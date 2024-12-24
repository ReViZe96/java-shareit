package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.SameEmailException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.user.dto.DeleteDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional(readOnly = true)
    public Collection<UserDto> getAllUsers() {
        log.info("Запрос списка всех пользователей");
        return userRepository.findAll().stream().map(userMapper::userToUserDto).toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        log.info("Запрос информации о пользователе с id = {}", userId);
        return userRepository.findById(userId).map(userMapper::userToUserDto).get();
    }

    @Transactional
    public UserDto addUser(UserDto newUser) {
        log.info("Получен запрос на добавление пользователя с именем {}", newUser.getName());
        checkUserEmail(newUser);
        log.info("Добавляемый пользователь валиден");
        User user = userMapper.userDtoToUser(newUser);
        return Optional.of(userRepository.save(user)).map(userMapper::userToUserDto).get();
    }

    @Transactional
    public UserDto updateUser(Long userId, UserDto updateUser) {
        log.info("Получен запрос на редактирование информации о пользователе с id = {}", userId);
        Optional<User> existUser = userRepository.findById(userId);
        if (existUser.isPresent()) {
            log.info("Обновляемый пользователь существует в системе");
            User updatingUser = existUser.get();
            if (updateUser.getEmail() != null) {
                checkUserEmail(updateUser);
                log.info("Новое значение электронной почты валидно");
                updatingUser.setEmail(updateUser.getEmail());
            }
            if (updateUser.getName() != null) {
                updatingUser.setName(updateUser.getName());
            }
            User user = userRepository.save(updatingUser);
            return userRepository.findById(user.getId()).map(userMapper::userToUserDto).get();
        } else {
            throw new NotFoundException("Обновляемый пользователь с id = " + userId + " не найден в системе");
        }
    }

    @Transactional
    public DeleteDto deleteAllUsers() {
        log.info("Получен запрос на удаление всех пользователей");
        userRepository.deleteAll();
        return new DeleteDto("Все пользователи успешно удалены");
    }

    @Transactional
    public void deleteUserById(Long userId) {
        log.info("Получен запрос на удаление пользователя с id {}", userId);
        userRepository.deleteById(userId);
    }


    private void checkUserEmail(UserDto user) {
        String email = user.getEmail();
        if (email == null || email.isEmpty() || email.isBlank()) {
            throw new ValidationException("У пользователя должен быть заполнен адрес электронной почты");
        }
        if (!email.contains("@") || email.contains(" ")) {
            throw new ValidationException("У пользователя " + user.getName() + " указан некорректный адрес электронной почты");
        }
        List<String> existEmails = userRepository.findAll().stream().map(User::getEmail).toList();
        if (existEmails.contains(email)) {
            throw new SameEmailException("Адрес электронной почты: " + email + " уже занят другим пользователем");
        }
    }

}
