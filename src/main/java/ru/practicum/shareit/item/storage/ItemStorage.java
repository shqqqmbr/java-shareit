package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item itemDto, int ownerId);

    Item updateItem(int itemId, Item itemDto, int ownerId);

    Item getItem(int itemId);

    List<Item> getAllUserItems(int ownerId);

    List<Item> getItemsByText(String text);
}
