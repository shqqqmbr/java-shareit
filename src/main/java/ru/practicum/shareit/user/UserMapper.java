package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}
