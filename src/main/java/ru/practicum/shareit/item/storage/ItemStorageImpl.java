package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private final ItemService service;

    public ItemStorageImpl(ItemService service) {
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
    public List<ItemDto> getItemsByText(String text) {
        return service.getItemsByText(text);
    }
}
