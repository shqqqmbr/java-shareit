package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.common.HttpHeaders;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingInputDto booking, @RequestHeader(HttpHeaders.SHARER_USER_ID) Integer ownerId) {
        return service.addBooking(booking, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Integer bookingId, @RequestParam Boolean approved, @RequestHeader(HttpHeaders.SHARER_USER_ID) Integer ownerId) {
        return service.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Integer bookingId, @RequestParam(defaultValue = "ALL", required = false) String state, @RequestHeader(HttpHeaders.SHARER_USER_ID) Integer ownerId) {
        return service.getBooking(bookingId, state, ownerId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookings(@RequestParam(defaultValue = "ALL") String state, @RequestHeader(HttpHeaders.SHARER_USER_ID) Integer ownerId) {
        return service.getAllUserBookings(state, ownerId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestParam(defaultValue = "ALL") String state, @RequestHeader(HttpHeaders.SHARER_USER_ID) Integer ownerId) {
        return service.getAllUserBookings(state, ownerId);
    }
}