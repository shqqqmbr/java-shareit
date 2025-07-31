package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserDto createUser(UserDto user) {
        User savedUser = userStorage.addUser(userMapper.toEntity(user));
        return userMapper.toDto(savedUser);
    }

    public UserDto updateUser(int userId, UserDto userUpdates) {
        User savedUser = userStorage.updateUser(userId, userMapper.toEntity(userUpdates));
        return userMapper.toDto(savedUser);
    }

    public UserDto getUserById(int userId) {
        User user = userStorage.getUser(userId);
        return userMapper.toDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(user -> userMapper.toDto(user))
                .collect(Collectors.toList());
    }

    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }
}