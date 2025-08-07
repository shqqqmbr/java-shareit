package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingInputDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingInputDto booking, int ownerId);

    BookingDto approveBooking(int bookingId, boolean approved, int ownerId);

    BookingDto getBooking(int bookingId, String state, int ownerId);

    List<BookingDto> getAllOwnerBookings(String state, int ownerId);

    List<BookingDto> getAllUserBookings(String state, int ownerId);
}
