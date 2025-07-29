package ru.practicum.shareit.user.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public void checkUserExists(int id){
        if(!users.containsKey(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
        User existingUser = getUserOrThrow(id);

        if (userUpdates == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Update data cannot be null");
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
        getUserOrThrow(id);
        users.remove(id);
    }

    @Override
    public User getUser(int id) {
        return getUserOrThrow(id);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    private User getUserOrThrow(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User with ID " + id + " not found"
            );
        }
        return user;
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be empty");
        }
        if (!user.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email must contain '@'");
        }
    }

    private void checkEmailUniqueness(String email) {
        users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .ifPresent(u -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Email " + email + " already exists"
                    );
                });
    }
}