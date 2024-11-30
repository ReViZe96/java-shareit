package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    @Modifying
    @Query("Update User us set us.name = ?2 where us.id = ?1")
    void setNameById(Long userId, String name);

    @Modifying
    @Query("Update User us set us.email = ?2 where us.email = ?1")
    void setEmailById(Long userId, String email);

}
