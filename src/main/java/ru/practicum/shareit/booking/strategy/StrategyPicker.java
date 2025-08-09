package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;

@RequiredArgsConstructor
public class StrategyPicker {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public BookingStrategy pick(String state) {
        switch (state) {
            case "CURRENT":
                return new CurrentStrategy(bookingRepository, bookingMapper);
            case "PAST":
                return new PastStrategy(bookingRepository, bookingMapper);
            case "FUTURE":
                return new FutureStrategy(bookingRepository, bookingMapper);
            case "WAITING":
                return new WaitingStrategy(bookingRepository, bookingMapper);
            case "REJECTED":
                return new RejectedStrategy(bookingRepository, bookingMapper);
            case "ALL":
                return new AllStrategy(bookingRepository, bookingMapper);
            default:
                throw new BadRequestException("Invalid state");
        }
    }
}
