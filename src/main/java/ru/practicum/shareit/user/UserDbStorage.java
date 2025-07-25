package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDbStorage implements UserStorage {
    private final UserService service;

    public UserDbStorage(UserService service) {
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
