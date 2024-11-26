package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Просмотр списка всех пользователей.
     */
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Просмотр конкретного пользователя.
     *
     * @param userId идентификатор возвращаемого пользователя
     */
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * Добавление нового пользователя.
     *
     * @param newUser DTO добавляемого пользователя
     * @return DTO добавленного пользователя
     */
    @PostMapping
    public UserDto addUser(@RequestBody UserDto newUser) {
        return userService.addUser(newUser);
    }

    /**
     * Редактирование информации о пользователе.
     *
     * @param userId     идентификатор редактируемого пользователя
     * @param updateUser DTO, содержащее вносимые в информацию о пользователе изменения
     * @return DTO измененного пользователя
     */
    @PutMapping
    public UserDto updateUser(@PathVariable Long userId,
                              @RequestBody UserDto updateUser) {
        return userService.updateUser(userId, updateUser);
    }

    /**
     * Удаление всех пользователей из системы.
     */
    @DeleteMapping
    public void deleteAllUsers() {
        userService.deleteAllUsers();
    }

    /**
     * Удаление конкретного пользователя.
     *
     * @param userId идентификатор удаляемого пользователя
     */
    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

}
