package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwner(User owner);

    Optional<Item> findById(Long id);

    Optional<Item> findByName(String name);

    List<Item> findAllByNameContainingIgnoreCase(String nameSearch);

    List<Item> findAllByDescriptionContainingIgnoreCase(String descriptionSearch);

}
