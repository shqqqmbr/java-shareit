package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_ShouldSaveUserSuccessfully() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
        UserDto result = userService.createUser(userDto);
        assertNotNull(result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        User savedUser = userRepository.findById(result.getId()).orElseThrow();
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowConflictException() {
        User existingUser = new User();
        existingUser.setName("Existing User");
        existingUser.setEmail("duplicate@example.com");
        userRepository.save(existingUser);
        UserDto newUserDto = new UserDto();
        newUserDto.setName("New User");
        newUserDto.setEmail("duplicate@example.com");
        assertThrows(ConflictException.class, () -> {
            userService.createUser(newUserDto);
        });
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {
        User existingUser = new User();
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        User savedUser = userRepository.save(existingUser);
        UserDto updates = new UserDto();
        updates.setName("New Name");
        updates.setEmail("new@example.com");
        UserDto result = userService.updateUser(savedUser.getId(), updates);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertEquals("New Name", updatedUser.getName());
        assertEquals("new@example.com", updatedUser.getEmail());
    }

    @Test
    void updateUser_WithDuplicateEmail_ShouldThrowConflictException() {
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        User savedUser1 = userRepository.save(user1);
        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);
        UserDto updates = new UserDto();
        updates.setName("Updated Name");
        updates.setEmail("user2@example.com");
        assertThrows(ConflictException.class, () -> {
            userService.updateUser(savedUser1.getId(), updates);
        });
    }

    @Test
    void updateUser_WithPartialUpdates_ShouldUpdateOnlyProvidedFields() {
        User existingUser = new User();
        existingUser.setName("Original Name");
        existingUser.setEmail("original@example.com");
        User savedUser = userRepository.save(existingUser);
        UserDto updates = new UserDto();
        updates.setName("Updated Name");
        UserDto result = userService.updateUser(savedUser.getId(), updates);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("original@example.com", result.getEmail());
    }

    @Test
    void getUserById_ShouldReturnCorrectUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        User savedUser = userRepository.save(user);
        UserDto result = userService.getUserById(savedUser.getId());
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserById_WithNonExistentId_ShouldThrowException() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(999);
        });
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setName("User One");
        user1.setEmail("user1@example.com");
        userRepository.save(user1);
        User user2 = new User();
        user2.setName("User Two");
        user2.setEmail("user2@example.com");
        userRepository.save(user2);
        List<UserDto> users = userService.getAllUsers();
        assertEquals(2, users.size());
        assertThat(users).extracting(UserDto::getName)
                .containsExactlyInAnyOrder("User One", "User Two");
        assertThat(users).extracting(UserDto::getEmail)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com");
    }

    @Test
    void getAllUsers_WithEmptyDatabase_ShouldReturnEmptyList() {
        List<UserDto> users = userService.getAllUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void deleteUser_ShouldRemoveUserFromDatabase() {
        User user = new User();
        user.setName("To Delete");
        user.setEmail("delete@example.com");
        User savedUser = userRepository.save(user);
        assertTrue(userRepository.existsById(savedUser.getId()));
        userService.deleteUser(savedUser.getId());
        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void deleteUser_WithNonExistentId_ShouldNotThrowException() {
        assertDoesNotThrow(() -> {
            userService.deleteUser(999);
        });
    }

    @Test
    void createUser_WithEmptyName_ShouldSaveUser() {
        UserDto userDto = new UserDto();
        userDto.setName("");
        userDto.setEmail("empty@example.com");
        UserDto result = userService.createUser(userDto);
        assertNotNull(result.getId());
        assertEquals("", result.getName());
        assertEquals("empty@example.com", result.getEmail());
    }

    @Test
    void updateUser_WithSameEmail_ShouldThrowException() {
        User existingUser = new User();
        existingUser.setName("Test User");
        existingUser.setEmail("test@example.com");
        User savedUser = userRepository.save(existingUser);
        UserDto updates = new UserDto();
        updates.setName("Updated Name");
        updates.setEmail("test@example.com");
        assertThrows(ConflictException.class, () -> {
            UserDto result = userService.updateUser(savedUser.getId(), updates);
            assertEquals("Updated Name", result.getName());
            assertEquals("test@example.com", result.getEmail());
        });
    }
}