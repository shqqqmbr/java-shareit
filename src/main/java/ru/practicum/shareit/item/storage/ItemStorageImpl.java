package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorageImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final UserStorageImpl storage;

    @Override
    public Item addItem(Item itemDto, int ownerId) {
        storage.checkUserExists(ownerId);
        itemDto.setId(idGenerator.getAndIncrement());
        itemDto.setOwner(ownerId);
        items.put(itemDto.getId(), itemDto);
        return itemDto;
    }

    @Override
    public Item updateItem(int itemId, Item itemDto, int ownerId) {
        storage.checkUserExists(ownerId);
        itemDto.setId(itemId);
        return itemDto;
    }

    @Override
    public Item getItem(int itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllUserItems(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String searchText = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }
}