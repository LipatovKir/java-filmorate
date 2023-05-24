package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ru.yandex.practicum.filmorate.model.Mappers.LIKE_MAPPER;

@RequiredArgsConstructor
@Repository("LikeStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    final String INSERT_INTO_LIKES = "INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?, ?)";
    final String DELETE_FROM_LIKES = "DELETE FROM LIKES WHERE (FILM_ID = ? AND USER_ID = ?)";
    final String SELECT_USER_FROM_LIKES = "SELECT USER_ID FROM LIKES WHERE FILM_ID =?";
    final String SELECT_FROM_LIKES = "SELECT * FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";

    @Override
    public void addLike(Long filmById, Long userById) {
        jdbcTemplate.update(INSERT_INTO_LIKES, filmById, userById);
    }

    @Override
    public void removeLike(Long filmById, Long userById) {
        jdbcTemplate.update(DELETE_FROM_LIKES, filmById, userById);
    }

    @Override
    public List<Long> getLikeByIdFilm(Long filmById) {
        return jdbcTemplate.query(SELECT_USER_FROM_LIKES, (rs, rowNum) -> rs.getLong("user_id"), filmById);
    }

    @Override
    public boolean isExist(Long filmById, Long userById) {
        try {
            jdbcTemplate.queryForObject(SELECT_FROM_LIKES, LIKE_MAPPER, filmById, userById);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            return false;
        }
    }
}

