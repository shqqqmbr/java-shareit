package ru.practicum.shareit.booking.strategy;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.BookingDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AllStrategyAbstract extends AbstractBookingStrategy {

    public AllStrategyAbstract(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        super(bookingRepository, bookingMapper);
    }

    @Override
    public State getState() {
        return State.ALL;
    }

    @Override
    public List<BookingDto> findBookings(int ownerId) {
        return bookingRepository.findByOwnerIdOrderByStartDesc(ownerId).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
