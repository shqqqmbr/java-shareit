package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingInputDto;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> addBooking(@Valid @RequestBody BookingInputDto booking, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return client.addBooking(booking, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Integer bookingId, @RequestParam Boolean approved, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return client.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Integer bookingId, @RequestParam(defaultValue = "ALL", required = false) String state, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return client.getBooking(bookingId, state, ownerId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBookings(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return client.getAllUserBookings(state, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return client.getAllUserBookings(state, ownerId);
    }
}
