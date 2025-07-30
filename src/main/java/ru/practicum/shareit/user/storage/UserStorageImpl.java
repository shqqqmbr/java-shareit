package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.BadRequestException;
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
    private final UserMapper userMapper;

    public void checkUserExists(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public UserDto addUser(UserDto user) {
        checkEmailUniqueness(user.getEmail());
        user.setId(idGenerator.getAndIncrement());
        users.put(user.getId(), userMapper.toEntity(user));
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public UserDto updateUser(int id, UserDto userUpdates) {
        checkUserExists(id);
        User existingUser = userMapper.toEntity(getUser(id));
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
        return userMapper.toDto(existingUser);
    }

    @Override
    public void deleteUser(int id) {
        checkUserExists(id);
        users.remove(id);
        emails.remove(users.get(id).getEmail());
    }

    @Override
    public UserDto getUser(int id) {
        return userMapper.toDto(users.get(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkEmailUniqueness(String email) {
        if (!emails.contains(email)) {
            throw new ConflictException("User with email " + email + " already exists");
        }
    }
}