package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.storage.UserStorageImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final Map<Integer, ItemDto> items = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final UserStorageImpl storage;

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        storage.checkUserExists(ownerId);
        itemDto.setId(idGenerator.getAndIncrement());
        itemDto.setOwner(ownerId);
        items.put(itemDto.getId(), itemDto);
        return itemDto;
    }

    @Override
    public ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId) {
        storage.checkUserExists(ownerId);
        ItemDto existingItem = getItemOrThrow(itemId);
        if (existingItem.getOwner() != ownerId) {
            throw new ForbiddenException("Editing someone else's property is prohibited");
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
        existingItem.setId(itemId);
        return existingItem;
    }

    @Override
    public ItemDto getItem(int itemId) {
        return getItemOrThrow(itemId);
    }

    @Override
    public List<ItemDto> getAllUserItems(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String searchText = text.toLowerCase();

        return items.values().stream()
                .filter(ItemDto::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }

    private ItemDto getItemOrThrow(int itemId) {
        ItemDto item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Item with ID " + itemId + " not found");
        }
        return item;
    }
}