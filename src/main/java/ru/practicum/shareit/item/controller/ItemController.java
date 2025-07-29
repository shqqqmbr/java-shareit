package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Valid
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return service.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable int id,@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-UserId") int ownerId) {
        return service.updateItem(id, itemDto, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@Valid @PathVariable int id) {
        return service.getItem(id);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return service.getAllUserItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestParam String text) {
        return service.getItemsByText(text);
    }
}
