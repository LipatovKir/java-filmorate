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
    final String SELECT_FROM_GENRE = "SELECT * FROM GENRE WHERE GENRE_ID = ?";

    @Override
    public Optional<Genre> findGenreById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_FROM_GENRE, GENRE_MAPPER, id));
        } catch (EmptyResultDataAccessException exception) {
            throw new ObjectNotFoundException("Жанр с id " + id + " не найден");
        }
    }

    @Override
    public List<Genre> findAllGenres() {
        String sql = "SELECT * FROM GENRE ORDER BY GENRE_ID";
        return jdbcTemplate.query(sql, GENRE_MAPPER);
    }
}
