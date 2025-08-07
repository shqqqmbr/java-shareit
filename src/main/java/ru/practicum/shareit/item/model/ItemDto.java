package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingInputDto;

import java.util.List;

@Getter
@Setter
public class ItemDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Integer owner;
    private BookingInputDto lastBooking;
    private BookingInputDto nextBooking;
    private List<CommentDto> comments;
}
