package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.HttpHeaders;
import ru.practicum.shareit.request.model.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody @Validated ItemRequestDto dto, @RequestHeader(HttpHeaders.SHARER_USER_ID) Integer ownerId) {
        return service.addItemRequest(dto, ownerId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllUserItemRequests(@RequestHeader(value = HttpHeaders.SHARER_USER_ID, required = false) Integer ownerId) {
        return service.getAllItemRequests(ownerId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@PathVariable("requestId") int requestId) {
        return service.getItemRequest(requestId);
    }

}
