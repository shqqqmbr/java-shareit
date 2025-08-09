package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.BookingDto;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WaitingStrategy implements BookingStrategy {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public List<BookingDto> findBookings(int ownerId) {
        return bookingRepository.findByitemOwnerIdAndStatus(ownerId, Status.WAITING).stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
