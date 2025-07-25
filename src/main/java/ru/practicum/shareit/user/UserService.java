package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User addUser(User user) {
        existsByEmail(user.getEmail());
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con ->  {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            return ps;
        }, keyHolder);
        if(keyHolder.getKey()!=null){
            user.setId(keyHolder.getKey().intValue());
        } else {
            throw new RuntimeException("Не удалось получить сгенерированный ID");
        }
        return user;
    }

    public User updateUser(int id, User user) {
        checkUserPresence(id);
        existsByEmail(user.getEmail());
        List<String> updates = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (user.getName() != null) {
            updates.add("name = ?");
            params.add(user.getName());
        }
        if (user.getEmail() != null) {
            updates.add("email = ?");
            params.add(user.getEmail());
        }
        if (updates.isEmpty()) {
            return user;
        }
        String sql = "UPDATE users SET " + String.join(", ", updates) + " WHERE id = ?";
        params.add(id);
        jdbcTemplate.update(sql, params.toArray());
        user.setId(id);
        return user;
    }

    public void deleteUser(int id) {
        checkUserPresence(id);
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public User getUser(int id){
        checkUserPresence(id);
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new UserMapper(), id);
    }

    public List<User> getAllUsers(){
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    private void checkUserPresence(int id) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, id);
        if (count == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id=" + id + " не найден");
        }
    }

    private void existsByEmail(String email) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, email);
        if (count > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с email=" + email + " уже существует");
        }
    }
}
