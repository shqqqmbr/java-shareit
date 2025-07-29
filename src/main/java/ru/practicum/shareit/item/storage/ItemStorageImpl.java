package ru.practicum.shareit.item.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.storage.UserStorageImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Integer, ItemDto> items = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final UserStorageImpl storage = new UserStorageImpl();

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        storage.checkUserExists(ownerId);
        validateItem(itemDto);
        itemDto.setId(idGenerator.getAndIncrement());
        itemDto.setOwner(ownerId);
        items.put(itemDto.getId(), itemDto);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId) {
        ItemDto existingItem = getItemOrThrow(itemId);
        if (existingItem.getOwner() != ownerId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User with ID " + ownerId + " is not the owner of item " + itemId);
        }
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        return existingItem;
    }

    @Override
    public ItemDto getItem(int itemId) {
        return getItemOrThrow(itemId);
    }

    @Override
    public List<ItemDto> getAllUserItems(int ownerId) {
        return items.values().stream().filter(item -> item.getOwner() == ownerId).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String searchText = text.toLowerCase();

        return items.values().stream().filter(ItemDto::getAvailable).filter(item -> item.getName().toLowerCase().contains(searchText) || item.getDescription().toLowerCase().contains(searchText)).collect(Collectors.toList());
    }

    private ItemDto getItemOrThrow(int itemId) {
        ItemDto item = items.get(itemId);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item with ID " + itemId + " not found");
        }
        return item;
    }

    private void validateItem(ItemDto item) {
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item cannot be null");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item name cannot be empty");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item description cannot be empty");
        }
        if (item.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item availability must be specified");
        }
    }
}