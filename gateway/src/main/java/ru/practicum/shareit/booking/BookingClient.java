package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.model.BookingInputDto;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(BookingInputDto dto, Integer ownerId) {
        return post("", ownerId, dto);
    }

    public ResponseEntity<Object> approveBooking(Integer bookingId, Boolean approved, Integer ownerId) {
        return patch("/" + bookingId + "?approved=" + approved, ownerId);
    }

    public ResponseEntity<Object> getBooking(Integer bookingId, Integer userId) {
        return get("/{bookingId}", userId, bookingId);
    }

    public ResponseEntity<Object> getAllUserBookings(String state, Integer ownerId) {
        return get("/owner?state={state}" + state, ownerId);
    }
}
