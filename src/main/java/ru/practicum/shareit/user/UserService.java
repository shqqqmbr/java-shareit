package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(UserDto user) {
        return userStorage.addUser(user);
    }

    public UserDto updateUser(int userId, UserDto userUpdates) {
        return userStorage.updateUser(userId, userUpdates);
    }

    public UserDto getUserById(int userId) {
        return userStorage.getUser(userId);
    }

    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }
}