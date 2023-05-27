package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.Mappers.GENRE_MAPPER;

@RequiredArgsConstructor
@Repository("GenreStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_FROM_GENRE_WHERE_GENRE_ID = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
    private static final String SELECT_FROM_GENRE_ORDER = "SELECT * FROM GENRE ORDER BY GENRE_ID";

    @Override
    public Optional<Genre> findGenreById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_FROM_GENRE_WHERE_GENRE_ID, GENRE_MAPPER, id));
        } catch (EmptyResultDataAccessException exception) {
            throw new ObjectNotFoundException("Жанр с id " + id + " не найден");
        }
    }

    @Override
    public List<Genre> findAllGenres() {
        return jdbcTemplate.query(SELECT_FROM_GENRE_ORDER, GENRE_MAPPER);
    }
}
