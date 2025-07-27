package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(int id, User user);

    void deleteUser(int id);

    User getUser(int id);

    List<User> getAllUsers();
}
