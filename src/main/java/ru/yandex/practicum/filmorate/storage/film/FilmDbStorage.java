package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.Mappers.FILM_MAPPER;
import static ru.yandex.practicum.filmorate.model.Mappers.GENRE_MAPPER;

@Slf4j
@RequiredArgsConstructor
@Repository("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    final String SELECT_FROM_FILMS = "SELECT * FROM FILMS";
    final String INSERT_INTO_FILMS = "INSERT INTO FIlMS (NAME, DESCRIPTION, RELEASEDATE, DURATION, MPA_ID) VALUES (?, ?, ?, ?, ?)";
    final String UPDATE_FILMS_SET_NAME = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
    final String DELETE_FROM_FILMS = "DELETE FROM FILMS WHERE FILM_ID = ?";
    final String SELECT_FROM_FILMS_FILM_ID = "SELECT * FROM FILMS WHERE FILM_ID = ?";
    final String INSERT_INTO_FILMS_GENRE = "INSERT INTO FILMS_GENRE(FILM_ID, GENRE_ID) VALUES (?, ?)";
    final String DELETE_FROM_FILM_GENRE = "DELETE FROM FILMS_GENRE WHERE FILM_ID = ?";
    final String SELECT_FROM_FILM_GENRE_JOIN_GENRE = "SELECT g.* FROM FILMS_GENRE AS fg " + "JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID " + "WHERE fg.FILM_ID =? " + "ORDER BY g.GENRE_ID";

    @Override
    public List<Film> getFilms() {
        try {
            return jdbcTemplate.query(SELECT_FROM_FILMS, FILM_MAPPER);
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_INTO_FILMS, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        Film newFilm = new Film(Objects.requireNonNull(keyHolder.getKey()).longValue(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa());
        newFilm.getGenres().addAll(film.getGenres());
        for (Genre genre : newFilm.getGenres()) {
            addGenreToFilm(newFilm.getId(), genre.getId());
        }
        return newFilm;
    }

    @Override
    public void putFilm(Film film) {
        this.jdbcTemplate.update(UPDATE_FILMS_SET_NAME, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());
    }

    @Override
    public void deleteFilm(Film film) {
        this.jdbcTemplate.update(DELETE_FROM_FILMS, film.getId());
    }

    @Override
    public Optional<Film> findFilmById(Long filmById) {
        try {
            Film film = jdbcTemplate.queryForObject(SELECT_FROM_FILMS_FILM_ID, FILM_MAPPER, filmById);
            assert film != null;
            return Optional.of(film);
        } catch (EmptyResultDataAccessException exception) {
            log.info("Фильм id{} не найден.", filmById);
            return Optional.empty();
        }
    }

    @Override
    public void addGenreToFilm(long filmById, long genreById) {
        jdbcTemplate.update(INSERT_INTO_FILMS_GENRE, filmById, genreById);
    }

    @Override
    public List<Genre> getGenreFilmById(long id) {
        return jdbcTemplate.query(SELECT_FROM_FILM_GENRE_JOIN_GENRE, GENRE_MAPPER, id);
    }

    @Override
    public void removeGenreFilm(long filmById) {
        jdbcTemplate.update(DELETE_FROM_FILM_GENRE, filmById);
    }

    @Override
    public boolean existsById(Long id) {
        return findFilmById(id).isPresent();
    }
}
