package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.common.HttpHeaders;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService service;

    @InjectMocks
    private ItemController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ItemDto testItemDto;
    private CommentDto testCommentDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testItemDto = new ItemDto();
        testItemDto.setId(1);
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Description");
        testItemDto.setAvailable(true);

        testCommentDto = new CommentDto();
        testCommentDto.setId(1);
        testCommentDto.setText("Test comment");
        testCommentDto.setAuthorName("Test User");
    }

    @Test
    void addItem_ShouldReturnCreatedItem() throws Exception {
        when(service.addItem(any(ItemDto.class), eq(1))).thenReturn(testItemDto);
        mockMvc.perform(post("/items")
                        .header(HttpHeaders.SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void addItem_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        when(service.updateItem(eq(1), any(ItemDto.class), eq(1))).thenReturn(testItemDto);
        mockMvc.perform(patch("/items/1")
                        .header(HttpHeaders.SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        when(service.getItem(1)).thenReturn(testItemDto);
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void getAllUserItems_ShouldReturnListOfItems() throws Exception {
        List<ItemDto> items = List.of(testItemDto);
        when(service.getAllUserItems(1)).thenReturn(items);
        mockMvc.perform(get("/items")
                        .header(HttpHeaders.SHARER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }

    @Test
    void getAllUserItems_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemsByText_ShouldReturnMatchingItems() throws Exception {
        List<ItemDto> items = List.of(testItemDto);
        when(service.getItemsByText("test")).thenReturn(items);
        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }

    @Test
    void getItemsByText_WithEmptyText_ShouldReturnEmptyList() throws Exception {
        when(service.getItemsByText("")).thenReturn(List.of());
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addComment_ShouldReturnCreatedComment() throws Exception {
        when(service.addComment(any(CommentDto.class), eq(1), eq(1))).thenReturn(testCommentDto);
        mockMvc.perform(post("/items/1/comment")
                        .header(HttpHeaders.SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Test comment"))
                .andExpect(jsonPath("$.authorName").value("Test User"));
    }

    @Test
    void addComment_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCommentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_WithInvalidComment_ShouldReturnBadRequest() throws Exception {
        CommentDto invalidComment = new CommentDto();
        invalidComment.setText("");
        mockMvc.perform(post("/items/1/comment")
                        .header(HttpHeaders.SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isOk());
    }
}