package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public class ItemDbStorage implements ItemStorage {
    private final ItemService service;

    public ItemDbStorage(ItemService service) {
        this.service = service;
    }

    @Override
    public ItemDto addItem(ItemDto item, int ownerId) {
        return service.addItem(item, ownerId);
    }

    @Override
    public ItemDto updateItem(int id, ItemDto item, int ownerId) {
        return service.updateItem(id, item, ownerId);
    }

    @Override
    public ItemDto getItem(int id, int ownerId) {
        return service.getItem(id, ownerId);
    }

    @Override
    public List<ItemDto> getAllUserItems(int ownerId) {
        return service.getAllUserItems(ownerId);
    }

    @Override
    public List<ItemDto> getItemsByText(String text, int ownerId) {
        return service.getItemsByText(text, ownerId);
    }
}
