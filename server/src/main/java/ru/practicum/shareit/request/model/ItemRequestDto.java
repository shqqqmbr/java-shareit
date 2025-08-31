package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDto {
    private Integer id;
    private String description;
    private Integer requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}
