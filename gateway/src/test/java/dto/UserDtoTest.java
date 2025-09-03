package dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.model.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = {ShareItGateway.class})
class UserDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testFullSerialization() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");
        String jsonString = objectMapper.writeValueAsString(userDto);
        assertThat(jsonString).contains("\"id\":1");
        assertThat(jsonString).contains("\"name\":\"John Doe\"");
        assertThat(jsonString).contains("\"email\":\"john.doe@example.com\"");
    }

    @Test
    void testFullDeserialization() throws Exception {
        String content = "{" +
                "\"id\": 123," +
                "\"name\": \"Alice Smith\"," +
                "\"email\": \"alice.smith@example.com\"" +
                "}";
        UserDto userDto = objectMapper.readValue(content, UserDto.class);
        assertThat(userDto.getId()).isEqualTo(123);
        assertThat(userDto.getName()).isEqualTo("Alice Smith");
        assertThat(userDto.getEmail()).isEqualTo("alice.smith@example.com");
    }

    @Test
    void testMinimalDeserialization() throws Exception {
        String content = "{" +
                "\"name\": \"Bob Johnson\"," +
                "\"email\": \"bob.johnson@example.com\"" +
                "}";
        UserDto userDto = objectMapper.readValue(content, UserDto.class);
        assertThat(userDto.getName()).isEqualTo("Bob Johnson");
        assertThat(userDto.getEmail()).isEqualTo("bob.johnson@example.com");
        assertThat(userDto.getId()).isNull();
    }

    @Test
    void testWithNullFields() throws Exception {
        String content = "{" +
                "\"id\": null," +
                "\"name\": \"Test User\"," +
                "\"email\": \"test@example.com\"" +
                "}";
        UserDto userDto = objectMapper.readValue(content, UserDto.class);
        assertThat(userDto.getId()).isNull();
        assertThat(userDto.getName()).isEqualTo("Test User");
        assertThat(userDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testEmptyObject() throws Exception {
        String content = "{}";
        UserDto userDto = objectMapper.readValue(content, UserDto.class);
        assertThat(userDto.getId()).isNull();
        assertThat(userDto.getName()).isNull();
        assertThat(userDto.getEmail()).isNull();
    }

    @Test
    void testPartialData() throws Exception {
        String content = "{" +
                "\"name\": \"Partial User\"" +
                "}";
        UserDto userDto = objectMapper.readValue(content, UserDto.class);
        assertThat(userDto.getName()).isEqualTo("Partial User");
        assertThat(userDto.getId()).isNull();
        assertThat(userDto.getEmail()).isNull();
    }

    @Test
    void testEmailCaseInsensitive() throws Exception {
        String content = "{" +
                "\"name\": \"Email Test\"," +
                "\"email\": \"TEST.USER@EXAMPLE.COM\"" +
                "}";
        UserDto userDto = objectMapper.readValue(content, UserDto.class);
        assertThat(userDto.getEmail()).isEqualTo("TEST.USER@EXAMPLE.COM");
    }

    @Test
    void testSpecialCharactersInName() throws Exception {
        String content = "{" +
                "\"name\": \"María José O'Connor-Smith\"," +
                "\"email\": \"maria@example.com\"" +
                "}";
        UserDto userDto = objectMapper.readValue(content, UserDto.class);
        assertThat(userDto.getName()).isEqualTo("María José O'Connor-Smith");
        assertThat(userDto.getEmail()).isEqualTo("maria@example.com");
    }

    @Test
    void testEmailWithPlusAddress() throws Exception {
        String content = "{" +
                "\"name\": \"Plus Test\"," +
                "\"email\": \"user+tag@example.com\"" +
                "}";
        UserDto userDto = objectMapper.readValue(content, UserDto.class);
        assertThat(userDto.getEmail()).isEqualTo("user+tag@example.com");
    }

    @Test
    void testEmailWithSubdomain() throws Exception {
        String content = "{" +
                "\"name\": \"Subdomain Test\"," +
                "\"email\": \"user@sub.domain.example.com\"" +
                "}";
        UserDto userDto = objectMapper.readValue(content, UserDto.class);
        assertThat(userDto.getEmail()).isEqualTo("user@sub.domain.example.com");
    }
}