package ru.practicum.shareit.item.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.item.dto.ItemDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper implements RowMapper<ItemDto> {

    @Override
    public ItemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        ItemDto dto = new ItemDto();
        dto.setId(rs.getInt("id"));
        dto.setName(rs.getString("name"));
        dto.setDescription(rs.getString("description"));
        dto.setAvailable(rs.getBoolean("available"));
        return dto;
    }
}
