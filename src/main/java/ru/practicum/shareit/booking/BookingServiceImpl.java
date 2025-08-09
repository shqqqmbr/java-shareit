package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.booking.strategy.BookingStrategy;
import ru.practicum.shareit.booking.strategy.StrategyPicker;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addBooking(BookingInputDto booking, int ownerId) {
        int itemId = booking.getItemId();
        Booking bookingEntity = bookingMapper.toBooking(booking);
        User userEntity = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available");
        }
        bookingEntity.setItem(item);
        bookingEntity.setBooker(userEntity);
        bookingEntity.setStatus(Status.WAITING);
        Booking savedBooking = bookingRepository.save(bookingEntity);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingDto approveBooking(int bookingId, boolean approved, int ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new ForbiddenException("User is not owner of this Booking");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getBooking(int bookingId, String state, int ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getItem().getOwner().getId() == ownerId || booking.getBooker().getId() == ownerId) {
            return bookingMapper.toDto(booking);
        }
        throw new NotFoundException("User is not owner of this Booking");
    }

    //    Здесь убрал идентичный метод, вся логика поиска находится в JPQL-запросе
    @Override
    public List<BookingDto> getAllUserBookings(String state, int ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        BookingStrategy strategy = new StrategyPicker(bookingRepository, bookingMapper).pick(state);
        return strategy.findBookings(ownerId);
    }
}
