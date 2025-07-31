package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);


    @Override
    public User addUser(User user) {
        checkEmailUniqueness(user.getEmail());
        user.setId(idGenerator.getAndIncrement());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(int id, User userUpdates) {
        checkEmailUniqueness(userUpdates.getEmail());
        checkUserExists(id);
        emails.remove(users.get(id).getEmail());
        userUpdates.setId(id);
        emails.add(userUpdates.getEmail());
        return userUpdates;
    }

    @Override
    public void deleteUser(int id) {
        checkUserExists(id);
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public User getUser(int id) {
        checkUserExists(id);
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream()
                .collect(Collectors.toList());
    }

    //    Думал перенести эти две проверки в сервис,
//    но не захотел нарушать интерфейс хранилища пользователя, чтобы
//    там остались только CRUD методы
    public void checkUserExists(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    private void checkEmailUniqueness(String email) {
        if (emails.contains(email)) {
            throw new ConflictException("User with email " + email + " already exists");
        }
    }
}