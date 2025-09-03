package dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.model.BookingInputDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JsonTest
@ContextConfiguration(classes = {ShareItGateway.class})
class BookingInputDtoJsonTest {

    @Autowired
    private JacksonTester<BookingInputDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 16, 12, 0);
        BookingInputDto dto = new BookingInputDto();
        dto.setId(1);
        dto.setItemId(42);
        dto.setStart(startTime);
        dto.setEnd(endTime);
        JsonContent<BookingInputDto> jsonContent = json.write(dto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(42);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-01-15T10:30:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-01-16T12:00:00");
    }

    @Test
    void testDeserialization() throws Exception {
        String content = "{" +
                "\"id\": 123," +
                "\"itemId\": 456," +
                "\"start\": \"2024-02-01T14:30:00\"," +
                "\"end\": \"2024-02-02T16:45:00\"" +
                "}";
        BookingInputDto dto = json.parseObject(content);
        assertThat(dto.getId()).isEqualTo(123);
        assertThat(dto.getItemId()).isEqualTo(456);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 2, 1, 14, 30));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2024, 2, 2, 16, 45));
    }

    @Test
    void testDeserializationWithMissingFields() throws Exception {
        String content = "{" +
                "\"id\": 123," +
                "\"itemId\": 456" +
                "}";
        BookingInputDto dto = json.parseObject(content);
        assertThat(dto.getId()).isEqualTo(123);
        assertThat(dto.getItemId()).isEqualTo(456);
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }

    @Test
    void testDeserializationWithNullDates() throws Exception {
        String content = "{" +
                "\"id\": 123," +
                "\"itemId\": 456," +
                "\"start\": null," +
                "\"end\": null" +
                "}";
        BookingInputDto dto = json.parseObject(content);
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }

    @Test
    void testInvalidDateTimeFormat() {
        String invalidContent = "{" +
                "\"itemId\": 456," +
                "\"start\": \"2024-13-45T25:61:00\"," +
                "\"end\": \"2024-02-02T16:45:00\"" +
                "}";
        assertThrows(JsonProcessingException.class, () -> {
            json.parseObject(invalidContent);
        });
    }

    @Test
    void testEmptyObject() throws Exception {
        String content = "{}";
        BookingInputDto dto = json.parseObject(content);
        assertThat(dto.getId()).isNull();
        assertThat(dto.getItemId()).isNull();
        assertThat(dto.getStart()).isNull();
        assertThat(dto.getEnd()).isNull();
    }

    @Test
    void testPartialData() throws Exception {
        String content = "{" +
                "\"itemId\": 789," +
                "\"start\": \"2024-03-01T10:00:00\"" +
                "}";
        BookingInputDto dto = json.parseObject(content);
        assertThat(dto.getId()).isNull();
        assertThat(dto.getItemId()).isEqualTo(789);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 3, 1, 10, 0));
        assertThat(dto.getEnd()).isNull();
    }

    @Test
    void testDateTimeSerializationFormat() throws Exception {
        LocalDateTime preciseTime = LocalDateTime.of(2024, 6, 15, 14, 30, 45, 123000000);
        BookingInputDto dto = new BookingInputDto();
        dto.setStart(preciseTime);
        dto.setEnd(preciseTime.plusHours(2));
        String jsonString = objectMapper.writeValueAsString(dto);
        assertThat(jsonString).contains("\"start\":\"2024-06-15T14:30:45.123\"");
        assertThat(jsonString).contains("\"end\":\"2024-06-15T16:30:45.123\"");
    }
}