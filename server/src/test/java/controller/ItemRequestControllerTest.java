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
import ru.practicum.shareit.common.HttpHeaders;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@ContextConfiguration(classes = ShareItServer.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    private ItemDto createItemDto(Integer id, String name, String description, Boolean available, Integer requestId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        itemDto.setRequestId(requestId);
        return itemDto;
    }

    @Test
    void createItemRequest_ShouldReturnCreatedRequest() throws Exception {
        Integer userId = 1;
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setDescription("Need a drill");
        ItemRequestDto createdDto = new ItemRequestDto();
        createdDto.setId(1);
        createdDto.setDescription("Need a drill");
        createdDto.setRequestor(userId);
        createdDto.setCreated(LocalDateTime.now());
        when(itemRequestService.addItemRequest(any(ItemRequestDto.class), eq(userId))).thenReturn(createdDto);
        mockMvc.perform(post("/requests")
                        .header(HttpHeaders.SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.requestor").value(userId))
                .andExpect(jsonPath("$.created").exists());
        verify(itemRequestService, times(1)).addItemRequest(any(ItemRequestDto.class), eq(userId));
    }

    @Test
    void createItemRequest_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setDescription("Need a drill");
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).addItemRequest(any(ItemRequestDto.class), anyInt());
    }

    @Test
    void getAllUserItemRequests_WithUserId_ShouldReturnListWithItems() throws Exception {
        Integer userId = 1;
        ItemDto item1 = createItemDto(1, "Drill", "Powerful drill", true, 1);
        ItemDto item2 = createItemDto(2, "Hammer", "Good hammer", true, 2);
        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1);
        request1.setDescription("Need a drill");
        request1.setRequestor(userId);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        request1.setItems(List.of(item1));
        ItemRequestDto request2 = new ItemRequestDto();
        request2.setId(2);
        request2.setDescription("Need a hammer");
        request2.setRequestor(userId);
        request2.setCreated(LocalDateTime.now());
        request2.setItems(List.of(item2));
        List<ItemRequestDto> requests = List.of(request1, request2);
        when(itemRequestService.getAllItemRequests(userId)).thenReturn(requests);
        mockMvc.perform(get("/requests")
                        .header(HttpHeaders.SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a drill"))
                .andExpect(jsonPath("$[0].requestor").value(userId))
                .andExpect(jsonPath("$[0].items.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].id").value(1))
                .andExpect(jsonPath("$[0].items[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Need a hammer"))
                .andExpect(jsonPath("$[1].requestor").value(userId))
                .andExpect(jsonPath("$[1].items.length()").value(1))
                .andExpect(jsonPath("$[1].items[0].id").value(2))
                .andExpect(jsonPath("$[1].items[0].name").value("Hammer"));
        verify(itemRequestService, times(1)).getAllItemRequests(userId);
    }

    @Test
    void getAllUserItemRequests_WithoutUserId_ShouldReturnList() throws Exception {
        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1);
        request1.setDescription("Need a drill");
        request1.setRequestor(2);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        List<ItemRequestDto> requests = List.of(request1);
        when(itemRequestService.getAllItemRequests(null)).thenReturn(requests);
        mockMvc.perform(get("/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a drill"))
                .andExpect(jsonPath("$[0].requestor").value(2));
        verify(itemRequestService, times(1)).getAllItemRequests(null);
    }

    @Test
    void getItemRequest_ShouldReturnRequestWithItems() throws Exception {
        int requestId = 1;
        Integer userId = 2;
        ItemDto item1 = createItemDto(1, "Drill", "Powerful drill", true, requestId);
        ItemDto item2 = createItemDto(2, "Screwdriver", "Set of screwdrivers", true, requestId);
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(requestId);
        requestDto.setDescription("Need tools");
        requestDto.setRequestor(userId);
        requestDto.setCreated(LocalDateTime.now());
        requestDto.setItems(List.of(item1, item2));
        when(itemRequestService.getItemRequest(requestId)).thenReturn(requestDto);
        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need tools"))
                .andExpect(jsonPath("$.requestor").value(userId))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Drill"))
                .andExpect(jsonPath("$.items[1].id").value(2))
                .andExpect(jsonPath("$.items[1].name").value("Screwdriver"));
        verify(itemRequestService, times(1)).getItemRequest(requestId);
    }

    @Test
    void getItemRequest_WhenNotFound_ShouldReturnEmpty() throws Exception {
        int requestId = 999;
        when(itemRequestService.getItemRequest(requestId)).thenReturn(null);
        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(itemRequestService, times(1)).getItemRequest(requestId);
    }

    @Test
    void getItemRequest_WithEmptyItemsList_ShouldReturnRequest() throws Exception {
        int requestId = 1;
        Integer userId = 2;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(requestId);
        requestDto.setDescription("Need tools");
        requestDto.setRequestor(userId);
        requestDto.setCreated(LocalDateTime.now());
        requestDto.setItems(List.of());
        when(itemRequestService.getItemRequest(requestId)).thenReturn(requestDto);
        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need tools"))
                .andExpect(jsonPath("$.requestor").value(userId))
                .andExpect(jsonPath("$.items.length()").value(0));
        verify(itemRequestService, times(1)).getItemRequest(requestId);
    }

    @Test
    void createItemRequest_WithEmptyDescription_ShouldStillCallService() throws Exception {
        Integer userId = 1;
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setDescription("");
        ItemRequestDto createdDto = new ItemRequestDto();
        createdDto.setId(1);
        createdDto.setDescription("");
        createdDto.setRequestor(userId);
        createdDto.setCreated(LocalDateTime.now());
        when(itemRequestService.addItemRequest(any(ItemRequestDto.class), eq(userId))).thenReturn(createdDto);
        mockMvc.perform(post("/requests")
                        .header(HttpHeaders.SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(""));
        verify(itemRequestService, times(1)).addItemRequest(any(ItemRequestDto.class), eq(userId));
    }
}