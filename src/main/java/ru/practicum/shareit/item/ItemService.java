package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId);

    ItemDto getItem(int itemId);

    List<ItemDto> getAllUserItems(int ownerId);

    List<ItemDto> getItemsByText(String text);

    CommentDto addComment(CommentDto comment, int itemId, int ownerId);
}
