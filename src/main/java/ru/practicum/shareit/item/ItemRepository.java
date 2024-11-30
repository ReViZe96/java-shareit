package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwner(User owner);

    Optional<Item> findById(Long id);

    @Modifying
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Query("Update Item it set it.name = ?2 where it.id = ?1")
    void setNameById(Long itemId, String name);

    @Modifying
    @Query("Update Item it set it.description = ?2 where it.id = ?1")
    void setDescriptionById(Long itemId, String description);

    @Modifying
    @Query("Update Item it set it.available = ?2 where it.id = ?1")
    void setAvailableById(Long itemId, Boolean available);

    @Modifying
    @Query("Update Item it set it.owner = ?2 where it.id = ?1")
    void setOwnerById(Long itemId, Long ownerId);

    List<Item> findAllByNameContainingIgnoreCase(String nameSearch);

    List<Item> findAllByDescriptionContainingIgnoreCase(String descriptionSearch);

}
