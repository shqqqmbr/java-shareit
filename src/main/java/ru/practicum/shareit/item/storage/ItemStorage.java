package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    ItemDto addItem(ItemDto item, int ownerId);

    ItemDto updateItem(int itemId, ItemDto item, int ownerId);

    ItemDto getItem(int itemId, int ownerI);

    List<ItemDto> getAllUserItems(int ownerId);

    List<ItemDto> getItemsByText(String text);
}
