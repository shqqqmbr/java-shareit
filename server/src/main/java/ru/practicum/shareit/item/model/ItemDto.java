package ru.practicum.shareit.item.model;



import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingInputDto;

import java.util.List;

@Getter
@Setter
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private BookingInputDto lastBooking;
    private BookingInputDto nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;
}
