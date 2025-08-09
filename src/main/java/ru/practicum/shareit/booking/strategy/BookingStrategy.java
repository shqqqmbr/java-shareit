package ru.practicum.shareit.booking.strategy;

import ru.practicum.shareit.booking.model.BookingDto;

import java.util.List;

public interface BookingStrategy {
    List<BookingDto> findBookings(int ownerId);
}
