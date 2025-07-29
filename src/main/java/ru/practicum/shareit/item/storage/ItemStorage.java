package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    ItemDto addItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId);

    ItemDto getItem(int itemId);

    List<ItemDto> getAllUserItems(int ownerId);

    List<ItemDto> getItemsByText(String text);
}
