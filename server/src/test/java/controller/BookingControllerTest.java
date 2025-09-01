package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.HttpHeaders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService service;

    @InjectMocks
    private BookingController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private BookingInputDto testBookingInputDto;
    private BookingDto testBookingDto;
    private ItemDto testItemDto;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testItemDto = new ItemDto();
        testItemDto.setId(1);
        testItemDto.setName("Test Item");

        testUserDto = new UserDto();
        testUserDto.setId(1);
        testUserDto.setName("Test User");

        testBookingInputDto = new BookingInputDto();
        testBookingInputDto.setItemId(1);
        testBookingInputDto.setStart(LocalDateTime.now().plusDays(1));
        testBookingInputDto.setEnd(LocalDateTime.now().plusDays(2));

        testBookingDto = new BookingDto();
        testBookingDto.setId(1);
        testBookingDto.setStart(LocalDateTime.now().plusDays(1));
        testBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        testBookingDto.setItem(testItemDto);
        testBookingDto.setBooker(testUserDto);
        testBookingDto.setStatus(Status.WAITING);
    }

    @Test
    void addBooking_ShouldReturnCreatedBooking() throws Exception {
        when(service.addBooking(any(BookingInputDto.class), eq(1))).thenReturn(testBookingDto);
        mockMvc.perform(post("/bookings")
                        .header(HttpHeaders.SHARER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingInputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Test Item"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.booker.name").value("Test User"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void addBooking_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingInputDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking_ShouldReturnApprovedBooking() throws Exception {
        BookingDto approvedBooking = new BookingDto();
        approvedBooking.setId(1);
        approvedBooking.setItem(testItemDto);
        approvedBooking.setBooker(testUserDto);
        approvedBooking.setStatus(Status.APPROVED);
        when(service.approveBooking(eq(1), eq(true), eq(1))).thenReturn(approvedBooking);
        mockMvc.perform(patch("/bookings/1")
                        .header(HttpHeaders.SHARER_USER_ID, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approveBooking_ShouldReturnRejectedBooking() throws Exception {
        BookingDto rejectedBooking = new BookingDto();
        rejectedBooking.setId(1);
        rejectedBooking.setItem(testItemDto);
        rejectedBooking.setBooker(testUserDto);
        rejectedBooking.setStatus(Status.REJECTED);
        when(service.approveBooking(eq(1), eq(false), eq(1))).thenReturn(rejectedBooking);
        mockMvc.perform(patch("/bookings/1")
                        .header(HttpHeaders.SHARER_USER_ID, 1)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void approveBooking_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking_WithoutApprovedParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .header(HttpHeaders.SHARER_USER_ID, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooking_ShouldReturnBooking() throws Exception {
        when(service.getBooking(eq(1), eq("ALL"), eq(1))).thenReturn(testBookingDto);
        mockMvc.perform(get("/bookings/1")
                        .header(HttpHeaders.SHARER_USER_ID, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getBooking_WithoutStateParam_ShouldUseDefault() throws Exception {
        when(service.getBooking(eq(1), eq("ALL"), eq(1))).thenReturn(testBookingDto);
        mockMvc.perform(get("/bookings/1")
                        .header(HttpHeaders.SHARER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getBooking_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/1")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOwnerBookings_ShouldReturnBookingsList() throws Exception {
        List<BookingDto> bookings = List.of(testBookingDto);
        when(service.getAllUserBookings(eq("CURRENT"), eq(1))).thenReturn(bookings);
        mockMvc.perform(get("/bookings/owner")
                        .header(HttpHeaders.SHARER_USER_ID, 1)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getAllOwnerBookings_WithoutStateParam_ShouldUseDefault() throws Exception {
        List<BookingDto> bookings = List.of(testBookingDto);
        when(service.getAllUserBookings(eq("ALL"), eq(1))).thenReturn(bookings);
        mockMvc.perform(get("/bookings/owner")
                        .header(HttpHeaders.SHARER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllOwnerBookings_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUserBookings_ShouldReturnBookingsList() throws Exception {
        List<BookingDto> bookings = List.of(testBookingDto);
        when(service.getAllUserBookings(eq("FUTURE"), eq(1))).thenReturn(bookings);
        mockMvc.perform(get("/bookings")
                        .header(HttpHeaders.SHARER_USER_ID, 1)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].booker.id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getAllUserBookings_WithoutStateParam_ShouldUseDefault() throws Exception {
        List<BookingDto> bookings = List.of(testBookingDto);
        when(service.getAllUserBookings(eq("ALL"), eq(1))).thenReturn(bookings);
        mockMvc.perform(get("/bookings")
                        .header(HttpHeaders.SHARER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllUserBookings_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooking_WithAllValidStates_ShouldWorkCorrectly() throws Exception {
        String[] validStates = {"CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED", "ALL"};
        for (String state : validStates) {
            when(service.getBooking(eq(1), eq(state), eq(1))).thenReturn(testBookingDto);
            mockMvc.perform(get("/bookings/1")
                            .header(HttpHeaders.SHARER_USER_ID, 1)
                            .param("state", state))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Test
    void getAllUserBookings_WithAllValidStates_ShouldWorkCorrectly() throws Exception {
        String[] validStates = {"CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED", "ALL"};
        List<BookingDto> bookings = List.of(testBookingDto);
        for (String state : validStates) {
            when(service.getAllUserBookings(eq(state), eq(1))).thenReturn(bookings);
            mockMvc.perform(get("/bookings")
                            .header(HttpHeaders.SHARER_USER_ID, 1)
                            .param("state", state))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }
    }

    @Test
    void getAllOwnerBookings_WithAllValidStates_ShouldWorkCorrectly() throws Exception {
        String[] validStates = {"CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED", "ALL"};
        List<BookingDto> bookings = List.of(testBookingDto);
        for (String state : validStates) {
            when(service.getAllUserBookings(eq(state), eq(1))).thenReturn(bookings);
            mockMvc.perform(get("/bookings/owner")
                            .header(HttpHeaders.SHARER_USER_ID, 1)
                            .param("state", state))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }
    }
}