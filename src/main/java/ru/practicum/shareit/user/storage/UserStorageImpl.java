package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Repository
public class UserStorageImpl implements UserStorage {
    private final UserService service;

    public UserStorageImpl(UserService service) {
        this.service = service;
    }

    @Override
    public User addUser(User user) {
        return service.addUser(user);
    }

    @Override
    public User updateUser(int id, User user) {
        return service.updateUser(id, user);
    }

    @Override
    public void deleteUser(int id) {
        service.deleteUser(id);
    }

    @Override
    public User getUser(int id) {
        return service.getUser(id);
    }

    @Override
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }
}
