package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface ItemRequestMapper {
    @Mapping(target = "requestor", source = "requestor.id")
    @Mapping(target = "items", source = "items")
    ItemRequestDto toDto(ItemRequest item);

    @Mapping(target = "requestor.id", source = "requestor")
    @Mapping(target = "items", source = "items")
    ItemRequest toEntity(ItemRequestDto itemRequestDto);
}
