package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * Просмотр списка всех пользователей.
     */
    @GetMapping
    public ResponseEntity<Collection<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Просмотр конкретного пользователя.
     *
     * @param userId идентификатор возвращаемого пользователя
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    /**
     * Добавление нового пользователя.
     *
     * @param newUser DTO добавляемого пользователя
     * @return DTO добавленного пользователя
     */
    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto newUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(newUser));
    }

    /**
     * Редактирование информации о пользователе.
     *
     * @param userId     идентификатор редактируемого пользователя
     * @param updateUser DTO, содержащее вносимые в информацию о пользователе изменения
     * @return DTO измененного пользователя
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId,
                                              @RequestBody UserDto updateUser) {
        return ResponseEntity.ok(userService.updateUser(userId, updateUser));
    }

    /**
     * Удаление всех пользователей из системы.
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok().body("Все пользователи успешно удалены");
    }

    /**
     * Удаление конкретного пользователя.
     *
     * @param userId идентификатор удаляемого пользователя
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok().body("Пользователь с id = " + userId + "удалён");
    }

}
