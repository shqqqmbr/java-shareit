package ru.practicum.shareit.item;

import org.mapstruct.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "userToOwnerId")
    ItemDto toDto(Item item);

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
    Item updateItemFromDto(ItemDto itemDto, @MappingTarget Item item);
}
