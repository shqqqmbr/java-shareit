package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return client.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable Integer id, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return client.updateItem(id, ownerId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@Valid @PathVariable Integer id) {
        return client.getItem(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return client.getAllUserItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestParam String text) {
        return client.getItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody CommentDto comment, @PathVariable Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return client.addComment(comment, itemId, ownerId);
    }
}
