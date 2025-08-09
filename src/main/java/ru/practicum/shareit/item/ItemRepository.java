package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwnerId(Integer ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE ((LOWER(i.name) LIKE LOWER(:text)) " +
            "OR (LOWER(i.description) LIKE LOWER(:text))) " +
            "AND i.available = true")
    List<Item> searchAvailableItemsByText(String text);
}

