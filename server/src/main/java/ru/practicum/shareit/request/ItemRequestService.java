package ru.practicum.shareit.request;

import ru.practicum.shareit.request.model.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, int ownerId);

    List<ItemRequestDto> getAllItemRequests(Integer ownerId);

    ItemRequestDto getItemRequest(int requestId);
}
