package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public void checkUserExists(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public User addUser(User user) {
        validateUser(user);
        checkEmailUniqueness(user.getEmail());
        user.setId(idGenerator.getAndIncrement());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(int id, User userUpdates) {
        User existingUser = getUser(id);
        if (userUpdates == null) {
            throw new BadRequestException("Update data cannot be null");
        }
        if (userUpdates.getName() != null) {
            existingUser.setName(userUpdates.getName());
        }
        if (userUpdates.getEmail() != null && !userUpdates.getEmail().equals(existingUser.getEmail())) {
            checkEmailUniqueness(userUpdates.getEmail());
            existingUser.setEmail(userUpdates.getEmail());
        }

        return existingUser;
    }

    @Override
    public void deleteUser(int id) {
        checkUserExists(id);
        users.remove(id);
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new BadRequestException("User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BadRequestException("Email cannot be blank");
        }
        if (!user.getEmail().contains("@")) {
            throw new BadRequestException("Email must contain '@'");
        }
    }

    private void checkEmailUniqueness(String email) {
        users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .ifPresent(u -> {
                    throw new ConflictException("User with email " + email + " already exists");
                });
    }
}