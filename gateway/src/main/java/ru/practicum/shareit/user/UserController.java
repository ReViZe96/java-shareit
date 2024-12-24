package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;


    /**
     * Просмотр списка всех пользователей.
     */
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    /**
     * Просмотр конкретного пользователя.
     *
     * @param userId идентификатор возвращаемого пользователя
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Get user with id = {}", userId);
        return userClient.getUserById(userId);
    }

    /**
     * Добавление нового пользователя.
     *
     * @param newUser DTO добавляемого пользователя
     * @return DTO добавленного пользователя
     */
    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto newUser) {
        log.info("Creating user");
        return userClient.addUser(newUser);
    }

    /**
     * Редактирование информации о пользователе.
     *
     * @param userId     идентификатор редактируемого пользователя
     * @param updateUser DTO, содержащее вносимые в информацию о пользователе изменения
     * @return DTO измененного пользователя
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody @Valid UserDto updateUser) {
        log.info("Update user with id = {}", userId);
        return userClient.updateUser(userId, updateUser);
    }

    /**
     * Удаление всех пользователей из системы.
     */
    @DeleteMapping
    public ResponseEntity<Object> deleteAllUsers() {
        log.info("Delete all users");
        return userClient.deleteAllUsers();
    }

    /**
     * Удаление конкретного пользователя.
     *
     * @param userId идентификатор удаляемого пользователя
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long userId) {
        log.info("Delete user with id = {}", userId);
        return userClient.deleteUserById(userId);
    }

}
