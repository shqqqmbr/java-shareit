package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserStorage {

    UserDto addUser(UserDto user);

    UserDto updateUser(int id, UserDto userUpdates);

    void deleteUser(int id);

    UserDto getUser(int id);

    List<UserDto> getAllUsers();
}