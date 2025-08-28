package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void addItemRequest_ShouldSaveRequestWithCorrectData() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        User savedUser = userRepository.save(user);
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");
        ItemRequestDto result = itemRequestService.addItemRequest(requestDto, savedUser.getId());
        assertNotNull(result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertEquals(savedUser.getId(), result.getRequestor());
        assertNotNull(result.getCreated());
        ItemRequest savedEntity = itemRequestRepository.findById(result.getId()).orElseThrow();
        assertEquals("Need a drill", savedEntity.getDescription());
        assertEquals(savedUser.getId(), savedEntity.getRequestor().getId());
    }

    @Test
    void getAllItemRequests_WithOwnerId_ShouldReturnOnlyUsersRequests() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@email.com");
        User savedUser1 = userRepository.save(user1);
        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@email.com");
        User savedUser2 = userRepository.save(user2);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Request 1");
        ItemRequestDto savedRequest1 = itemRequestService.addItemRequest(requestDto1, savedUser1.getId());
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Request 2");
        ItemRequestDto savedRequest2 = itemRequestService.addItemRequest(requestDto2, savedUser1.getId());
        ItemRequestDto requestDto3 = new ItemRequestDto();
        requestDto3.setDescription("Request 3");
        ItemRequestDto savedRequest3 = itemRequestService.addItemRequest(requestDto3, savedUser2.getId());
        List<ItemRequestDto> user1Requests = itemRequestService.getAllItemRequests(savedUser1.getId());
        List<ItemRequestDto> user2Requests = itemRequestService.getAllItemRequests(savedUser2.getId());
        assertEquals(2, user1Requests.size());
        assertEquals(1, user2Requests.size());

        assertThat(user1Requests).extracting(ItemRequestDto::getId)
                .containsExactlyInAnyOrder(savedRequest1.getId(), savedRequest2.getId());
        assertThat(user2Requests).extracting(ItemRequestDto::getId)
                .containsExactly(savedRequest3.getId());
    }

    @Test
    void getAllItemRequests_WithoutOwnerId_ShouldReturnAllRequests() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@email.com");
        User savedUser1 = userRepository.save(user1);
        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@email.com");
        User savedUser2 = userRepository.save(user2);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Request 1");
        itemRequestService.addItemRequest(requestDto1, savedUser1.getId());
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Request 2");
        itemRequestService.addItemRequest(requestDto2, savedUser2.getId());
        List<ItemRequestDto> allRequests = itemRequestService.getAllItemRequests(null);
        assertEquals(2, allRequests.size());
    }

    @Test
    void getItemRequest_ShouldReturnCorrectRequest() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        User savedUser = userRepository.save(user);
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test request");
        ItemRequestDto expectedRequest = itemRequestService.addItemRequest(requestDto, savedUser.getId());
        ItemRequestDto result = itemRequestService.getItemRequest(expectedRequest.getId());
        assertNotNull(result);
        assertEquals(expectedRequest.getId(), result.getId());
        assertEquals("Test request", result.getDescription());
        assertEquals(savedUser.getId(), result.getRequestor());
        assertEquals(expectedRequest.getCreated(), result.getCreated());
    }

    @Test
    void getItemRequest_WithNonExistentId_ShouldThrowException() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getItemRequest(999);
        });
    }

    @Test
    void getAllItemRequests_WithNonExistentUser_ShouldThrowException() {
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAllItemRequests(999);
        });
    }

    @Test
    void addItemRequest_WithNonExistentUser_ShouldThrowException() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test request");
        assertThrows(NotFoundException.class, () -> {
            itemRequestService.addItemRequest(requestDto, 999);
        });
    }

    @Test
    void requestsShouldBeOrderedByCreationDateDesc() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        User savedUser = userRepository.save(user);
        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setDescription("Request 1");
        ItemRequestDto request1 = itemRequestService.addItemRequest(requestDto1, savedUser.getId());
        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setDescription("Request 2");
        ItemRequestDto request2 = itemRequestService.addItemRequest(requestDto2, savedUser.getId());
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(savedUser.getId());
        assertEquals(2, requests.size());
        assertEquals(request1.getId(), requests.get(0).getId());
        assertEquals(request2.getId(), requests.get(1).getId());
        assertFalse(requests.get(0).getCreated().isAfter(requests.get(1).getCreated()));
    }

    @Test
    void getAllItemRequests_WithEmptyDatabase_ShouldReturnEmptyList() {
        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(null);
        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

    @Test
    void addItemRequest_ShouldSetCurrentDateTime() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        User savedUser = userRepository.save(user);
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test request");
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        ItemRequestDto result = itemRequestService.addItemRequest(requestDto, savedUser.getId());
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        assertNotNull(result.getCreated());
        assertTrue(result.getCreated().isAfter(beforeCreation));
        assertTrue(result.getCreated().isBefore(afterCreation));
    }
}