package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ItemService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ItemDto addItem(ItemDto item, int ownerId) {
        checkUserPresence(ownerId);
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        String sql = "INSERT INTO items (name, description, available)  VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setBoolean(3, item.getAvailable());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            item.setId(keyHolder.getKey().intValue());
        } else {
            throw new RuntimeException("Не удалось получить сгенерированный ID");
        }
        return item;
    }

    public ItemDto updateItem(int itemId, ItemDto item, int ownerId) {
        checkUserPresence(ownerId);
        checkItemPresence(itemId);
        List<String> updates = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (item.getName() != null) {
            updates.add("name = ?");
            params.add(item.getName());
        }
        if (item.getDescription() != null) {
            updates.add("description = ?");
            params.add(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updates.add("available = ?");
            params.add(item.getAvailable());
        }
        if (updates.isEmpty()) {
            return item;
        }
        String sql = "UPDATE items SET " + String.join(", ", updates) + " WHERE id = ?";
        params.add(itemId);
        jdbcTemplate.update(sql, params.toArray());
        item.setId(itemId);
        return item;
    }

    public ItemDto getItem(int itemId, int ownerId) {
        checkUserPresence(ownerId);
        checkItemPresence(itemId);
        String sql = "SELECT * FROM items WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new ItemMapper(), itemId);
    }

    public List<ItemDto> getAllUserItems(int ownerId) {
        checkUserPresence(ownerId);
        String sql = "SELECT id, name, description, available FROM items WHERE owner = ?";
        return jdbcTemplate.query(sql, new ItemMapper(), ownerId);
    }

    public List<ItemDto> getItemsByText(String text, int ownerId) {
        checkUserPresence(ownerId);
        if (text == null && text.isBlank()) {
            return Collections.emptyList();
        }
        String sql = """
                SELECT id, name, description, available 
                FROM items 
                WHERE (LOWER(name) LIKE LOWER(?) 
                   OR LOWER(description) LIKE LOWER(?))
                AND owner = ?
                """;
        return jdbcTemplate.query(sql, new ItemMapper(), text, text, ownerId);
    }

    private void checkUserPresence(int id) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);
        if (count == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id=" + id + " не найден");
        }
    }

    private void checkItemPresence(int id) {
        String checkSql = "SELECT COUNT(*) FROM items WHERE id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);
        if (count == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь с id=" + id + " не найдена");
        }
    }
}
