package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email already in use");
        }
        User savedUser = repository.save(userMapper.toEntity(user));
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(int userId, UserDto userUpdates) {
        userUpdates.setId(userId);
        if (repository.existsByEmail(userUpdates.getEmail())) {
            throw new ConflictException("Email already in use");
        }
        User existingUser = repository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        User updatedUser = userMapper.updateUserFromDto(userUpdates, existingUser);
        User savedUser = repository.save(updatedUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getUserById(int userId) {
        Optional<User> user = repository.findById(userId);
        return userMapper.toDto(user.get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(user -> userMapper.toDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(int userId) {
        repository.deleteById(userId);
    }
}