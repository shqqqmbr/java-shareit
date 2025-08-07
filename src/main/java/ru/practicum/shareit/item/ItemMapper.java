package ru.practicum.shareit.item;

import org.mapstruct.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "owner", source = "owner", qualifiedByName = "userToOwnerId")
    @Mapping(target = "comments", ignore = true)
    ItemDto toDto(Item item);

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "owner", qualifiedByName = "ownerIdToUser")
    Item toEntity(ItemDto itemDto);

    @Named("userToOwnerId")
    default Integer userToOwnerId(User user) {
        return user != null ? user.getId() : null;
    }

    @Named("ownerIdToUser")
    default User ownerIdToUser(Integer ownerId) {
        if (ownerId == null) return null;
        User owner = new User();
        owner.setId(ownerId);
        return owner;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Item updateItemFromDto(ItemDto itemDto, @MappingTarget Item item);
}
