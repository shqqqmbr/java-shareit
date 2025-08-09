package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto updateUser(int userId, UserDto userUpdates);

    UserDto getUserById(int userId);

    List<UserDto> getAllUsers();

    void deleteUser(int userId);
}
