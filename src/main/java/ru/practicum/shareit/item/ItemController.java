package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(HttpHeaders.SHARER_USER_ID) Integer ownerId) {
        return service.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Integer id, @RequestBody ItemDto itemDto, @RequestHeader(HttpHeaders.SHARER_USER_ID) Integer ownerId) {
        return service.updateItem(id, itemDto, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@Valid @PathVariable Integer id) {
        return service.getItem(id);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(HttpHeaders.SHARER_USER_ID) Integer ownerId) {
        return service.getAllUserItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestParam String text) {
        return service.getItemsByText(text);
    }
}
