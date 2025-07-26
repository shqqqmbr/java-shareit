package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    ItemDto addItem(ItemDto item, int ownerId);
    ItemDto updateItem(int itemId, ItemDto item, int ownerId);
    ItemDto getItem(int itemId, int ownerI);
    List<ItemDto> getAllUserItems(int ownerId);
    List<ItemDto> getItemsByText(String text, int ownerId);
}
