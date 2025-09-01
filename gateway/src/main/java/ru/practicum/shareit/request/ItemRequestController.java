package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.HttpHeaders;
import ru.practicum.shareit.request.model.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestBody @Validated ItemRequestDto dto, @RequestHeader(HttpHeaders.SHARER_USER_ID) Integer ownerId) {
        return client.addItemRequest(dto, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItemRequests(@RequestHeader(value = HttpHeaders.SHARER_USER_ID, required = false) Integer ownerId) {
        return client.getAllUserItemRequests(ownerId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable("requestId") int requestId) {
        return client.getItemRequest(requestId);
    }

}
