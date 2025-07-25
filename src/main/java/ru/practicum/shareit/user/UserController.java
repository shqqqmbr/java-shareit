package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Valid
public class UserController {
    private final UserStorage storage;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return storage.addUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable int userId, @RequestBody @Valid User user) {
        return storage.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        storage.deleteUser(userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        return storage.getUser(userId);
    }
}
