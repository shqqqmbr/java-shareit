package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ContextConfiguration(classes = {ShareItServer.class})
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private User booker;
    private User anotherUser;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@email.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@email.com");
        booker = userRepository.save(booker);

        anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@email.com");
        anotherUser = userRepository.save(anotherUser);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Need item for testing");
        itemRequest.setRequestor(booker);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
    }

    @Test
    void addItem_shouldAddItemWithoutRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto result = itemService.addItem(itemDto, owner.getId());
        assertNotNull(result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertNull(result.getRequestId());
    }

    @Test
    void addItem_shouldAddItemWithRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(itemRequest.getId());
        ItemDto result = itemService.addItem(itemDto, owner.getId());
        assertNotNull(result.getId());
        assertEquals("Test Item", result.getName());

    }

    @Test
    void updateItem_shouldUpdateItemSuccessfully() {
        ItemDto initialItem = new ItemDto();
        initialItem.setName("Initial Name");
        initialItem.setDescription("Initial Description");
        initialItem.setAvailable(true);
        ItemDto savedItem = itemService.addItem(initialItem, owner.getId());
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");
        updateDto.setAvailable(false);
        ItemDto result = itemService.updateItem(savedItem.getId(), updateDto, owner.getId());
        assertEquals(savedItem.getId(), result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void updateItem_shouldThrowExceptionWhenNotOwner() {
        ItemDto initialItem = new ItemDto();
        initialItem.setName("Test Item");
        initialItem.setDescription("Test Description");
        initialItem.setAvailable(true);
        ItemDto savedItem = itemService.addItem(initialItem, owner.getId());
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");
        assertThrows(NotFoundException.class, () ->
                itemService.updateItem(savedItem.getId(), updateDto, anotherUser.getId())
        );
    }

    @Test
    void getItem_shouldReturnItemWithComments() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto savedItem = itemService.addItem(itemDto, owner.getId());
        createBookingForCommentTest(savedItem.getId(), booker.getId());
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");
        itemService.addComment(commentDto, savedItem.getId(), booker.getId());
        ItemDto result = itemService.getItem(savedItem.getId());
        assertEquals(savedItem.getId(), result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void getAllUserItems_shouldReturnItemsWithBookings() {
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Item 1");
        itemDto1.setDescription("Description 1");
        itemDto1.setAvailable(true);
        ItemDto savedItem1 = itemService.addItem(itemDto1, owner.getId());
        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Item 2");
        itemDto2.setDescription("Description 2");
        itemDto2.setAvailable(true);
        ItemDto savedItem2 = itemService.addItem(itemDto2, owner.getId());
        createBookingForItem(savedItem1.getId(), booker.getId(),
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        createBookingForItem(savedItem2.getId(), booker.getId(),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        List<ItemDto> result = itemService.getAllUserItems(owner.getId());
        assertEquals(2, result.size());
        assertNotNull(result.get(0).getLastBooking());
        assertNotNull(result.get(1).getNextBooking());
    }

    @Test
    void getItemsByText_shouldReturnEmptyListForEmptyText() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemService.addItem(itemDto, owner.getId());
        List<ItemDto> result = itemService.getItemsByText("");
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_shouldAddCommentSuccessfully() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto savedItem = itemService.addItem(itemDto, owner.getId());
        createBookingForCommentTest(savedItem.getId(), booker.getId());
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Excellent item, worked perfectly!");
        CommentDto result = itemService.addComment(commentDto, savedItem.getId(), booker.getId());
        assertNotNull(result.getId());
        assertEquals("Excellent item, worked perfectly!", result.getText());
        assertEquals(booker.getName(), result.getAuthorName());
        assertNotNull(result.getCreated());
        ItemDto itemWithComments = itemService.getItem(savedItem.getId());
        assertEquals(1, itemWithComments.getComments().size());
    }

    @Test
    void addComment_shouldThrowExceptionWhenUserNeverBooked() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto savedItem = itemService.addItem(itemDto, owner.getId());
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Trying to comment without booking");
        assertThrows(BadRequestException.class, () ->
                itemService.addComment(commentDto, savedItem.getId(), anotherUser.getId())
        );
    }

    @Test
    void addComment_shouldThrowExceptionWhenBookingNotCompleted() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto savedItem = itemService.addItem(itemDto, owner.getId());
        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(savedItem.getId()).get());
        booking.setBooker(userRepository.findById(booker.getId()).get());
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Trying to comment during booking");
        assertThrows(BadRequestException.class, () ->
                itemService.addComment(commentDto, savedItem.getId(), booker.getId())
        );
    }

    private void createBookingForCommentTest(int itemId, int bookerId) {
        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(itemId).get());
        booking.setBooker(userRepository.findById(bookerId).get());
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);
    }

    private void createBookingForItem(int itemId, int bookerId, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setItem(itemRepository.findById(itemId).get());
        booking.setBooker(userRepository.findById(bookerId).get());
        booking.setStatus(Status.APPROVED);
        booking.setStart(start);
        booking.setEnd(end);
        bookingRepository.save(booking);
    }
}