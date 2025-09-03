package dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.model.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JsonTest
@ContextConfiguration(classes = {ShareItGateway.class})
class ItemRequestDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testFullSerialization() throws Exception {
        LocalDateTime createdTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        ItemDto item1 = new ItemDto();
        item1.setId(1);
        item1.setName("Drill");
        item1.setDescription("Powerful drill");
        item1.setAvailable(true);
        ItemDto item2 = new ItemDto();
        item2.setId(2);
        item2.setName("Hammer");
        item2.setDescription("Heavy hammer");
        item2.setAvailable(true);
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(100);
        requestDto.setDescription("Need tools for construction");
        requestDto.setRequestor(500);
        requestDto.setCreated(createdTime);
        requestDto.setItems(List.of(item1, item2));
        String jsonString = objectMapper.writeValueAsString(requestDto);
        assertThat(jsonString).contains("\"id\":100");
        assertThat(jsonString).contains("\"description\":\"Need tools for construction\"");
        assertThat(jsonString).contains("\"requestor\":500");
        assertThat(jsonString).contains("\"items\"");
        assertThat(jsonString).contains("\"name\":\"Drill\"");
        assertThat(jsonString).contains("\"name\":\"Hammer\"");
    }

    @Test
    void testFullDeserialization() throws Exception {
        String content = "{" +
                "\"id\": 150," +
                "\"description\": \"Looking for gardening tools\"," +
                "\"requestor\": 600," +
                "\"created\": \"2024-02-01T14:25:30\"," +
                "\"items\": [" +
                "{" +
                "\"id\": 10," +
                "\"name\": \"Shovel\"," +
                "\"description\": \"Garden shovel\"," +
                "\"available\": true," +
                "\"owner\": 600" +
                "}," +
                "{" +
                "\"id\": 11," +
                "\"name\": \"Rake\"," +
                "\"description\": \"Leaf rake\"," +
                "\"available\": false," +
                "\"owner\": 700" +
                "}" +
                "]" +
                "}";
        ItemRequestDto requestDto = objectMapper.readValue(content, ItemRequestDto.class);
        assertThat(requestDto.getId()).isEqualTo(150);
        assertThat(requestDto.getDescription()).isEqualTo("Looking for gardening tools");
        assertThat(requestDto.getRequestor()).isEqualTo(600);
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 2, 1, 14, 25, 30));
        assertThat(requestDto.getItems()).hasSize(2);
        assertThat(requestDto.getItems().get(0).getName()).isEqualTo("Shovel");
        assertThat(requestDto.getItems().get(1).getName()).isEqualTo("Rake");
        assertThat(requestDto.getItems().get(1).getAvailable()).isFalse();
    }

    @Test
    void testMinimalDeserialization() throws Exception {
        String content = "{" +
                "\"description\": \"Minimal request\"" +
                "}";
        ItemRequestDto requestDto = objectMapper.readValue(content, ItemRequestDto.class);
        assertThat(requestDto.getDescription()).isEqualTo("Minimal request");
        assertThat(requestDto.getId()).isNull();
        assertThat(requestDto.getRequestor()).isNull();
        assertThat(requestDto.getCreated()).isNull();
        assertThat(requestDto.getItems()).isNull();
    }

    @Test
    void testWithNullFields() throws Exception {
        String content = "{" +
                "\"description\": \"Test request\"," +
                "\"id\": null," +
                "\"requestor\": null," +
                "\"created\": null," +
                "\"items\": null" +
                "}";
        ItemRequestDto requestDto = objectMapper.readValue(content, ItemRequestDto.class);
        assertThat(requestDto.getDescription()).isEqualTo("Test request");
        assertThat(requestDto.getId()).isNull();
        assertThat(requestDto.getRequestor()).isNull();
        assertThat(requestDto.getCreated()).isNull();
        assertThat(requestDto.getItems()).isNull();
    }

    @Test
    void testEmptyItemsArray() throws Exception {
        String content = "{" +
                "\"description\": \"Empty items request\"," +
                "\"items\": []" +
                "}";
        ItemRequestDto requestDto = objectMapper.readValue(content, ItemRequestDto.class);
        assertThat(requestDto.getDescription()).isEqualTo("Empty items request");
        assertThat(requestDto.getItems()).isNotNull();
        assertThat(requestDto.getItems()).isEmpty();
    }

    @Test
    void testWithoutItemsField() throws Exception {
        String content = "{" +
                "\"id\": 200," +
                "\"description\": \"Request without items field\"," +
                "\"requestor\": 800," +
                "\"created\": \"2024-03-01T09:00:00\"" +
                "}";
        ItemRequestDto requestDto = objectMapper.readValue(content, ItemRequestDto.class);
        assertThat(requestDto.getId()).isEqualTo(200);
        assertThat(requestDto.getDescription()).isEqualTo("Request without items field");
        assertThat(requestDto.getRequestor()).isEqualTo(800);
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 3, 1, 9, 0));
        assertThat(requestDto.getItems()).isNull();
    }

    @Test
    void testDateTimePrecision() throws Exception {
        String content = "{" +
                "\"description\": \"Time precision test\"," +
                "\"created\": \"2024-01-15T10:30:45.123\"" +
                "}";
        ItemRequestDto requestDto = objectMapper.readValue(content, ItemRequestDto.class);
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30, 45, 123000000));
    }

    @Test
    void testInvalidDateTimeFormat() {
        String invalidContent = "{" +
                "\"description\": \"Invalid date\"," +
                "\"created\": \"2024-13-45T25:61:00\"" +
                "}";
        assertThrows(JsonProcessingException.class, () -> {
            objectMapper.readValue(invalidContent, ItemRequestDto.class);
        });
    }

    @Test
    void testComplexNestedStructure() throws Exception {
        String content = "{" +
                "\"id\": 300," +
                "\"description\": \"Office equipment needed\"," +
                "\"requestor\": 900," +
                "\"created\": \"2024-04-01T08:00:00\"," +
                "\"items\": [" +
                "{" +
                "\"id\": 20," +
                "\"name\": \"Monitor\"," +
                "\"description\": \"27 inch monitor\"," +
                "\"available\": true," +
                "\"owner\": 900," +
                "\"lastBooking\": {" +
                "\"id\": 1," +
                "\"itemId\": 20," +
                "\"start\": \"2024-03-15T10:00:00\"," +
                "\"end\": \"2024-03-16T18:00:00\"" +
                "}," +
                "\"nextBooking\": {" +
                "\"id\": 2," +
                "\"itemId\": 20," +
                "\"start\": \"2024-04-15T09:00:00\"," +
                "\"end\": \"2024-04-16T17:00:00\"" +
                "}" +
                "}" +
                "]" +
                "}";
        ItemRequestDto requestDto = objectMapper.readValue(content, ItemRequestDto.class);
        assertThat(requestDto.getItems()).hasSize(1);
        ItemDto item = requestDto.getItems().get(0);
        assertThat(item.getName()).isEqualTo("Monitor");
        assertThat(item.getLastBooking()).isNotNull();
        assertThat(item.getLastBooking().getId()).isEqualTo(1);
        assertThat(item.getNextBooking()).isNotNull();
        assertThat(item.getNextBooking().getItemId()).isEqualTo(20);
    }

    @Test
    void testSerializationWithNullCreated() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(400);
        requestDto.setDescription("Request with null created");
        requestDto.setCreated(null);
        String jsonString = objectMapper.writeValueAsString(requestDto);
        assertThat(jsonString).contains("\"id\":400");
        assertThat(jsonString).contains("\"description\":\"Request with null created\"");
        assertThat(jsonString).contains("\"created\":null");
    }
}