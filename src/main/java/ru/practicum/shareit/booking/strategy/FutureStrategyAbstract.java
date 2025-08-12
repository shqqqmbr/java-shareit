package ru.practicum.shareit.booking.strategy;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.BookingDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FutureStrategyAbstract extends AbstractBookingStrategy {

    public FutureStrategyAbstract(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        super(bookingRepository, bookingMapper);
    }

    @Override
    public State getState() {
        return State.FUTURE;
    }

    @Override
    public List<BookingDto> findBookings(int ownerId) {
        return bookingRepository.findByItemOwnerIdAndStartAfter(ownerId, LocalDateTime.now()).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
