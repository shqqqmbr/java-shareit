package ru.practicum.shareit.booking.strategy;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.BookingDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RejectedStrategyAbstract extends AbstractBookingStrategy {

    public RejectedStrategyAbstract(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        super(bookingRepository, bookingMapper);
    }

    @Override
    public State getState() {
        return State.REJECTED;
    }

    @Override
    public List<BookingDto> findBookings(int ownerId) {
        return bookingRepository.findByitemOwnerIdAndStatus(ownerId, Status.REJECTED).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
