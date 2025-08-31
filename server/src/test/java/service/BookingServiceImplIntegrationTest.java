package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ContextConfiguration(classes = {ShareItServer.class})
class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item availableItem;
    private Item unavailableItem;
    private BookingInputDto validBookingInput;
    private BookingInputDto pastBookingInput;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@email.com");
        booker = userRepository.save(booker);

        availableItem = new Item();
        availableItem.setName("Available Item");
        availableItem.setDescription("Test available item");
        availableItem.setAvailable(true);
        availableItem.setOwner(owner);
        availableItem = itemRepository.save(availableItem);

        unavailableItem = new Item();
        unavailableItem.setName("Unavailable Item");
        unavailableItem.setDescription("Test unavailable item");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwner(owner);
        unavailableItem = itemRepository.save(unavailableItem);

        validBookingInput = new BookingInputDto();
        validBookingInput.setItemId(availableItem.getId());
        validBookingInput.setStart(LocalDateTime.now().plusDays(1));
        validBookingInput.setEnd(LocalDateTime.now().plusDays(3));

        pastBookingInput = new BookingInputDto();
        pastBookingInput.setItemId(availableItem.getId());
        pastBookingInput.setStart(LocalDateTime.now().minusDays(3));
        pastBookingInput.setEnd(LocalDateTime.now().minusDays(1));
    }

    @Test
    void addBooking_WithValidData_ShouldCreateBooking() {
        BookingDto result = bookingService.addBooking(validBookingInput, booker.getId());
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(availableItem.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals("WAITING", result.getStatus().toString());
    }

    @Test
    void addBooking_WithUnavailableItem_ShouldThrowBadRequestException() {
        BookingInputDto bookingInput = new BookingInputDto();
        bookingInput.setItemId(unavailableItem.getId());
        bookingInput.setStart(LocalDateTime.now().plusDays(1));
        bookingInput.setEnd(LocalDateTime.now().plusDays(3));
        assertThrows(BadRequestException.class, () ->
                bookingService.addBooking(bookingInput, booker.getId())
        );
    }

    @Test
    void addBooking_WithNonExistentUser_ShouldThrowException() {
        assertThrows(Exception.class, () ->
                bookingService.addBooking(validBookingInput, 999)
        );
    }

    @Test
    void addBooking_WithNonExistentItem_ShouldThrowException() {
        BookingInputDto bookingInput = new BookingInputDto();
        bookingInput.setItemId(999);
        bookingInput.setStart(LocalDateTime.now().plusDays(1));
        bookingInput.setEnd(LocalDateTime.now().plusDays(3));
        assertThrows(Exception.class, () ->
                bookingService.addBooking(bookingInput, booker.getId())
        );
    }

    @Test
    void approveBooking_ByOwner_ShouldApproveBooking() {
        BookingDto createdBooking = bookingService.addBooking(validBookingInput, booker.getId());
        BookingDto result = bookingService.approveBooking(createdBooking.getId(), true, owner.getId());
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus().toString());
    }

    @Test
    void approveBooking_ByNonOwner_ShouldThrowForbiddenException() {
        BookingDto createdBooking = bookingService.addBooking(validBookingInput, booker.getId());
        assertThrows(ForbiddenException.class, () ->
                bookingService.approveBooking(createdBooking.getId(), true, booker.getId())
        );
    }

    @Test
    void approveBooking_NonExistentBooking_ShouldThrowException() {
        assertThrows(Exception.class, () ->
                bookingService.approveBooking(999, true, owner.getId())
        );
    }

    @Test
    void getBooking_ByOwner_ShouldReturnBooking() {
        BookingDto createdBooking = bookingService.addBooking(validBookingInput, booker.getId());
        BookingDto result = bookingService.getBooking(createdBooking.getId(), "ALL", owner.getId());
        assertNotNull(result);
        assertEquals(createdBooking.getId(), result.getId());
    }

    @Test
    void getBooking_ByBooker_ShouldReturnBooking() {
        BookingDto createdBooking = bookingService.addBooking(validBookingInput, booker.getId());
        BookingDto result = bookingService.getBooking(createdBooking.getId(), "ALL", booker.getId());
        assertNotNull(result);
        assertEquals(createdBooking.getId(), result.getId());
    }

    @Test
    void getBooking_ByUnauthorizedUser_ShouldThrowNotFoundException() {
        BookingDto createdBooking = bookingService.addBooking(validBookingInput, booker.getId());
        User unauthorizedUser = new User();
        unauthorizedUser.setName("Unauthorized");
        unauthorizedUser.setEmail("unauthorized@email.com");
        unauthorizedUser = userRepository.save(unauthorizedUser);
        User finalUnauthorizedUser = unauthorizedUser;
        assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(createdBooking.getId(), "ALL", finalUnauthorizedUser.getId())
        );
    }

    @Test
    void getAllUserBookings_WithExistingUser_ShouldReturnBookings() {
        bookingService.addBooking(validBookingInput, booker.getId());
        List<BookingDto> result = bookingService.getAllUserBookings("ALL", booker.getId());
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAllUserBookings_WithNonExistentUser_ShouldThrowException() {
        assertThrows(Exception.class, () ->
                bookingService.getAllUserBookings("ALL", 999)
        );
    }

    @Test
    void integrationFlow_CompleteBookingProcess_ShouldWorkCorrectly() {
        BookingDto createdBooking = bookingService.addBooking(validBookingInput, booker.getId());
        BookingDto approvedBooking = bookingService.approveBooking(createdBooking.getId(), true, owner.getId());
        BookingDto retrievedBooking = bookingService.getBooking(approvedBooking.getId(), "ALL", booker.getId());
        assertNotNull(createdBooking);
        assertNotNull(approvedBooking);
        assertNotNull(retrievedBooking);
        assertEquals(createdBooking.getId(), approvedBooking.getId());
        assertEquals(approvedBooking.getId(), retrievedBooking.getId());
        assertEquals("APPROVED", retrievedBooking.getStatus().toString());
    }
}