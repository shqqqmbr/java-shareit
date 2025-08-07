package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingInputDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDto toDto(Booking booking);

    Booking toBooking(BookingInputDto booking);

    @Mapping(target = "itemId", source = "item.id")
    BookingInputDto toInputDto(Booking booking);

}
