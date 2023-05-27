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
    private static final String INSERT_INTO_LIKES_FILM_ID_USER_ID_VALUES =
            "INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_FROM_LIKES_WHERE_FILM_ID_AND_USER_ID =
            "DELETE FROM LIKES WHERE (FILM_ID = ? AND USER_ID = ?)";
    private static final String SELECT_USER_ID_FROM_LIKES_WHERE_FILM_ID =
            "SELECT USER_ID FROM LIKES WHERE FILM_ID =?";
    private static final String SELECT_FROM_LIKES_WHERE_FILM_ID_AND_USER_ID =
            "SELECT * FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";

    @Override
    public void addLike(Long filmById, Long userById) {
        jdbcTemplate.update(INSERT_INTO_LIKES_FILM_ID_USER_ID_VALUES, filmById, userById);
    }

    @Override
    public void removeLike(Long filmById, Long userById) {
        jdbcTemplate.update(DELETE_FROM_LIKES_WHERE_FILM_ID_AND_USER_ID, filmById, userById);
    }

    @Override
    public List<Long> getLikeByIdFilm(Long filmById) {
        return jdbcTemplate.query(SELECT_USER_ID_FROM_LIKES_WHERE_FILM_ID,
                (rs, rowNum) -> rs.getLong("user_id"),
                filmById);
    }

    @Override
    public boolean isExist(Long filmById, Long userById) {
        try {
            jdbcTemplate.queryForObject(SELECT_FROM_LIKES_WHERE_FILM_ID_AND_USER_ID, LIKE_MAPPER, filmById, userById);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            return false;
        }
    }
}

