package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> getFilms();

    Film addFilm(Film film);

    void putFilm(Film film);

    void deleteFilm(Film film);

    Optional<Film> findFilmById(Long filmId);

    void addGenreToFilm(long filmId, long genreId);

    List<Genre> getGenreFilmById(long id);

    void removeGenreFilm(long filmId);
}

