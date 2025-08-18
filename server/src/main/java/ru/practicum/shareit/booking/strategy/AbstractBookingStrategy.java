package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.BookingDto;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractBookingStrategy {
    protected final BookingRepository bookingRepository;
    protected final BookingMapper bookingMapper;

    public abstract State getState();

    public abstract List<BookingDto> findBookings(int ownerId);
}
