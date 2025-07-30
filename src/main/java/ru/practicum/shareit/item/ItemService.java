package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage storage;

    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        return storage.addItem(itemDto, ownerId);
    }

    public ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId) {
        return storage.updateItem(itemId, itemDto, ownerId);
    }

    public ItemDto getItem(int itemId) {
        return storage.getItem(itemId);
    }

    public List<ItemDto> getAllUserItems(int ownerId) {
        return storage.getAllUserItems(ownerId);
    }

    public List<ItemDto> getItemsByText(String text) {
        return storage.getItemsByText(text);
    }
}
