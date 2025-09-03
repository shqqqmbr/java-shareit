package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = {ShareItGateway.class})
class ItemDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testFullSerialization() throws Exception {
        BookingInputDto lastBooking = new BookingInputDto();
        lastBooking.setId(1);
        lastBooking.setItemId(100);
        lastBooking.setStart(LocalDateTime.of(2024, 1, 15, 10, 0));
        lastBooking.setEnd(LocalDateTime.of(2024, 1, 16, 12, 0));
        BookingInputDto nextBooking = new BookingInputDto();
        nextBooking.setId(2);
        nextBooking.setItemId(100);
        nextBooking.setStart(LocalDateTime.of(2024, 2, 1, 14, 0));
        nextBooking.setEnd(LocalDateTime.of(2024, 2, 2, 16, 0));
        CommentDto comment = new CommentDto();
        comment.setId(1);
        comment.setText("Great item!");
        comment.setAuthorName("John Doe");
        comment.setCreated(LocalDateTime.of(2024, 1, 10, 9, 0));
        ItemDto itemDto = new ItemDto();
        itemDto.setId(100);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill for construction");
        itemDto.setAvailable(true);
        itemDto.setOwner(500);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(List.of(comment));
        itemDto.setRequestId(300);
        String jsonString = objectMapper.writeValueAsString(itemDto);
        assertThat(jsonString).contains("\"id\":100");
        assertThat(jsonString).contains("\"name\":\"Drill\"");
        assertThat(jsonString).contains("\"description\":\"Powerful drill for construction\"");
        assertThat(jsonString).contains("\"available\":true");
        assertThat(jsonString).contains("\"owner\":500");
        assertThat(jsonString).contains("\"requestId\":300");
        assertThat(jsonString).contains("\"lastBooking\"");
        assertThat(jsonString).contains("\"nextBooking\"");
        assertThat(jsonString).contains("\"comments\"");
    }

    @Test
    void testFullDeserialization() throws Exception {
        String content = "{" +
                "\"id\": 150," +
                "\"name\": \"Hammer\"," +
                "\"description\": \"Heavy duty hammer\"," +
                "\"available\": false," +
                "\"owner\": 600," +
                "\"lastBooking\": {" +
                "\"id\": 10," +
                "\"itemId\": 150," +
                "\"start\": \"2024-03-01T10:00:00\"," +
                "\"end\": \"2024-03-02T12:00:00\"" +
                "}," +
                "\"nextBooking\": {" +
                "\"id\": 11," +
                "\"itemId\": 150," +
                "\"start\": \"2024-04-01T14:00:00\"," +
                "\"end\": \"2024-04-02T16:00:00\"" +
                "}," +
                "\"comments\": [" +
                "{" +
                "\"id\": 5," +
                "\"text\": \"Good quality\"," +
                "\"authorName\": \"Alice\"," +
                "\"created\": \"2024-02-15T09:30:00\"" +
                "}" +
                "]," +
                "\"requestId\": 400" +
                "}";
        ItemDto itemDto = objectMapper.readValue(content, ItemDto.class);
        assertThat(itemDto.getId()).isEqualTo(150);
        assertThat(itemDto.getName()).isEqualTo("Hammer");
        assertThat(itemDto.getDescription()).isEqualTo("Heavy duty hammer");
        assertThat(itemDto.getAvailable()).isFalse();
        assertThat(itemDto.getOwner()).isEqualTo(600);
        assertThat(itemDto.getRequestId()).isEqualTo(400);
        assertThat(itemDto.getLastBooking()).isNotNull();
        assertThat(itemDto.getLastBooking().getId()).isEqualTo(10);
        assertThat(itemDto.getLastBooking().getItemId()).isEqualTo(150);
        assertThat(itemDto.getNextBooking()).isNotNull();
        assertThat(itemDto.getNextBooking().getId()).isEqualTo(11);
        assertThat(itemDto.getComments()).hasSize(1);
        assertThat(itemDto.getComments().get(0).getText()).isEqualTo("Good quality");
        assertThat(itemDto.getComments().get(0).getAuthorName()).isEqualTo("Alice");
    }

    @Test
    void testMinimalDeserialization() throws Exception {
        String content = "{" +
                "\"name\": \"Saw\"," +
                "\"description\": \"Circular saw\"," +
                "\"available\": true" +
                "}";
        ItemDto itemDto = objectMapper.readValue(content, ItemDto.class);
        assertThat(itemDto.getName()).isEqualTo("Saw");
        assertThat(itemDto.getDescription()).isEqualTo("Circular saw");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getId()).isNull();
        assertThat(itemDto.getOwner()).isNull();
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).isNull();
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    void testWithNullFields() throws Exception {
        String content = "{" +
                "\"name\": \"Test Item\"," +
                "\"description\": \"Test Description\"," +
                "\"available\": true," +
                "\"owner\": null," +
                "\"lastBooking\": null," +
                "\"nextBooking\": null," +
                "\"comments\": null," +
                "\"requestId\": null" +
                "}";
        ItemDto itemDto = objectMapper.readValue(content, ItemDto.class);
        assertThat(itemDto.getName()).isEqualTo("Test Item");
        assertThat(itemDto.getDescription()).isEqualTo("Test Description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getOwner()).isNull();
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).isNull();
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    void testEmptyCommentsArray() throws Exception {
        String content = "{" +
                "\"name\": \"Item\"," +
                "\"description\": \"Desc\"," +
                "\"available\": true," +
                "\"comments\": []" +
                "}";
        ItemDto itemDto = objectMapper.readValue(content, ItemDto.class);
        assertThat(itemDto.getComments()).isNotNull();
        assertThat(itemDto.getComments()).isEmpty();
    }

    @Test
    void testMissingRequiredFields() throws Exception {
        String content = "{" +
                "\"id\": 200," +
                "\"owner\": 700" +
                "}";
        ItemDto itemDto = objectMapper.readValue(content, ItemDto.class);
        assertThat(itemDto.getId()).isEqualTo(200);
        assertThat(itemDto.getOwner()).isEqualTo(700);
        assertThat(itemDto.getName()).isNull();
        assertThat(itemDto.getDescription()).isNull();
        assertThat(itemDto.getAvailable()).isNull();
    }

    @Test
    void testBooleanAvailableField() throws Exception {
        String contentTrue = "{" +
                "\"name\": \"Item1\"," +
                "\"description\": \"Desc1\"," +
                "\"available\": true" +
                "}";
        String contentFalse = "{" +
                "\"name\": \"Item2\"," +
                "\"description\": \"Desc2\"," +
                "\"available\": false" +
                "}";
        ItemDto itemDto1 = objectMapper.readValue(contentTrue, ItemDto.class);
        ItemDto itemDto2 = objectMapper.readValue(contentFalse, ItemDto.class);
        assertThat(itemDto1.getAvailable()).isTrue();
        assertThat(itemDto2.getAvailable()).isFalse();
    }

    @Test
    void testComplexNestedStructures() throws Exception {
        String content = "{" +
                "\"id\": 300," +
                "\"name\": \"Laptop\"," +
                "\"description\": \"Gaming laptop\"," +
                "\"available\": true," +
                "\"owner\": 800," +
                "\"comments\": [" +
                "{" +
                "\"id\": 1," +
                "\"text\": \"Fast performance\"," +
                "\"authorName\": \"User1\"," +
                "\"created\": \"2024-01-10T10:00:00\"" +
                "}," +
                "{" +
                "\"id\": 2," +
                "\"text\": \"Good display\"," +
                "\"authorName\": \"User2\"," +
                "\"created\": \"2024-01-12T14:30:00\"" +
                "}" +
                "]," +
                "\"requestId\": 500" +
                "}";
        ItemDto itemDto = objectMapper.readValue(content, ItemDto.class);
        assertThat(itemDto.getComments()).hasSize(2);
        assertThat(itemDto.getComments().get(0).getText()).isEqualTo("Fast performance");
        assertThat(itemDto.getComments().get(1).getAuthorName()).isEqualTo("User2");
        assertThat(itemDto.getRequestId()).isEqualTo(500);
    }
}