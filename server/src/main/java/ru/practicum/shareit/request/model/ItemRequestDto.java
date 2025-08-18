package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
public class ItemRequestDto {
    private Integer id;
    @NotBlank
    private String description;
    private Integer requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}
