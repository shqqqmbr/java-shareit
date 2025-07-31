package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    private final UserMapper mapper;

    public void checkUserExists(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public UserDto addUser(UserDto user) {
        checkEmailUniqueness(user.getEmail());
        user.setId(idGenerator.getAndIncrement());
        users.put(user.getId(), mapper.toEntity(user));
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public UserDto updateUser(int id, UserDto userUpdates) {
        checkUserExists(id);
        checkEmailUniqueness(userUpdates.getEmail());
        emails.remove(users.get(id).getEmail());
        userUpdates.setId(id);
        mapper.updateUserFromDto(userUpdates, users.get(id));
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
    public UserDto getUser(int id) {
        checkUserExists(id);
        return mapper.toDto(users.get(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkEmailUniqueness(String email) {
        if (emails.contains(email)) {
            throw new ConflictException("User with email " + email + " already exists");
        }
    }
}