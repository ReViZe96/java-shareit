package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component("databaseUserStorage")
@Primary
@RequiredArgsConstructor
public class DatabaseUserStorage implements UserStorage {

    private final UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<User> addUser(User newUser) {
        return Optional.of(userRepository.save(newUser));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateUser(Long userId, Map<String, Object> updatedField) {
        Set<Map.Entry<String, Object>> entry = updatedField.entrySet();
        User user = userRepository.findById(userId).get();
        for (Map.Entry<String, Object> field : entry) {
            if (field.getKey().equals(EMAIL)) {
                user.setEmail(field.getValue().toString());
            }
            if (field.getKey().equals(NAME)) {
                user.setName(field.getValue().toString());
            }
        }
        userRepository.save(user);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

}
