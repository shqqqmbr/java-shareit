package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = ShareItServer.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserDto inputUser = new UserDto(null, "John Doe", "john@example.com");
        UserDto createdUser = new UserDto(1, "John Doe", "john@example.com");
        when(userService.createUser(any(UserDto.class))).thenReturn(createdUser);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void createUser_WithEmptyName_ShouldStillCallService() throws Exception {
        UserDto invalidUser = new UserDto(null, "", "test@example.com");
        UserDto createdUser = new UserDto(1, "", "test@example.com");
        when(userService.createUser(any(UserDto.class))).thenReturn(createdUser);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isCreated());
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void createUser_WithInvalidEmail_ShouldStillCallService() throws Exception {
        UserDto invalidUser = new UserDto(null, "Test User", "invalid-email");
        UserDto createdUser = new UserDto(1, "Test User", "invalid-email");
        when(userService.createUser(any(UserDto.class))).thenReturn(createdUser);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isCreated());
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        Integer userId = 1;
        UserDto updateUser = new UserDto(null, "John Updated", "updated@example.com");
        UserDto updatedUser = new UserDto(userId, "John Updated", "updated@example.com");
        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(updatedUser);
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
        verify(userService, times(1)).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        Integer userId = 1;
        UserDto user = new UserDto(userId, "John Doe", "john@example.com");
        when(userService.getUserById(userId)).thenReturn(user);
        mockMvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturnEmpty() throws Exception {
        Integer userId = 999;
        when(userService.getUserById(userId)).thenReturn(null);
        mockMvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        UserDto user1 = new UserDto(1, "John Doe", "john@example.com");
        UserDto user2 = new UserDto(2, "Jane Smith", "jane@example.com");
        List<UserDto> users = List.of(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        Integer userId = 1;
        doNothing().when(userService).deleteUser(userId);
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());
        verify(userService, times(1)).deleteUser(userId);
    }
}