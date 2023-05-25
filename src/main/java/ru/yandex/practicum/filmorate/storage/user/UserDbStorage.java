package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.Mappers.USER_MAPPER;

@Slf4j
@Repository("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String setUsersSetEmailNameLogin = "UPDATE USERS SET EMAIL = ?, NAME = ?, LOGIN = ?, BIRTHDAY = ? WHERE USER_ID = ?";
    private static final String deleteFromUsersWhereUserId = "DELETE FROM USERS  WHERE USER_ID = ?";
    private static final String selectFromUsersWhereUser = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String selectIntoUsers = "SELECT * FROM USERS";
    private static final String insertIntoUsers = "INSERT INTO USERS (email, name, login, birthday) VALUES (?, ?, ?, ?)";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(insertIntoUsers, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        return new User(Objects.requireNonNull(keyHolder.getKey()).longValue(), user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
    }

    @Override
    public void putUser(User user) {
        jdbcTemplate.update(setUsersSetEmailNameLogin, user.getEmail(), user.getName(), user.getLogin(), java.sql.Date.valueOf(user.getBirthday()), user.getId());
    }

    @Override
    public Optional<User> findUserById(Long id) {
        try {
            User user = jdbcTemplate.queryForObject(selectFromUsersWhereUser, USER_MAPPER, id);
            assert user != null;
            return Optional.of(user);
        } catch (EmptyResultDataAccessException exception) {
            log.info("Пользователь с id {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsUserById(Long id) {
        return findUserById(id).isPresent();
    }

    @Override
    public void deleteUser(User user) {
        jdbcTemplate.update(deleteFromUsersWhereUserId, user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return jdbcTemplate.query(selectIntoUsers, USER_MAPPER);
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }
}

