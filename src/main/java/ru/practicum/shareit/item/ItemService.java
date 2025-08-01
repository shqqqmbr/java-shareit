package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage storage;
    private final ItemMapper itemMapper;

    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        Item item = itemMapper.toEntity(itemDto);
        Item savedItem = storage.addItem(item, ownerId);
        return itemMapper.toDto(savedItem);
    }

    public ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId) {
        Item item = itemMapper.toEntity(itemDto);
        Item savedItem = storage.updateItem(itemId, item, ownerId);
        return itemMapper.toDto(savedItem);
    }

    public ItemDto getItem(int itemId) {
        Item item = storage.getItem(itemId);
        return itemMapper.toDto(item);
    }

    public List<ItemDto> getAllUserItems(int ownerId) {
        return storage.getAllUserItems(ownerId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getItemsByText(String text) {
        return storage.getItemsByText(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}
