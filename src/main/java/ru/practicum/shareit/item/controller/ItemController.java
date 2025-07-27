package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemStorage storage;

    @Autowired
    public ItemController(ItemStorage storage) {
        this.storage = storage;
    }

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return storage.addItem(item, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable @Valid int id, @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return storage.updateItem(id, item, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable @Valid int id, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return storage.getItem(id, ownerId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return storage.getAllUserItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestParam String text) {
        return storage.getItemsByText(text);
    }
}
