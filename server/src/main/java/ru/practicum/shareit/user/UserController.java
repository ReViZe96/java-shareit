package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.DeleteDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public ResponseEntity<Collection<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto newUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(newUser));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId,
                                              @RequestBody UserDto updateUser) {
        return ResponseEntity.ok(userService.updateUser(userId, updateUser));
    }

    @DeleteMapping
    public ResponseEntity<DeleteDto> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok().body(new DeleteDto("Все пользователи успешно удалены"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<DeleteDto> deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok().body(new DeleteDto("Пользователь с id = " + userId + " удалён"));
    }

}
