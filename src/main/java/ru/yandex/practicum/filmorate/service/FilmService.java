package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WorkApplicationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;

    public List<Film> findAllFilms() {
        List<Film> films = new ArrayList<>();
        for (Film film : filmStorage.getFilms()) {
            Film film1 = new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), mpaStorage.findMpaById(film.getMpa().getId()).get());
            film1.getGenres().addAll(filmStorage.getGenreFilmById(film1.getId()));
            film1.getLikes().addAll(likeStorage.getLikeByIdFilm(film.getId()));
            films.add(film1);
        }
        log.info("Список всех фильмов ");
        return films;
    }

    public Film createFilm(Film film) {
        if (Validator.validateFilm(film) && !Validator.validateReleaseDateFilm(film)) {
            Film film1 = appointGenre(film);
            log.info("Добавлен новый фильм: {}", film.getName());
            return filmStorage.addFilm(film1);
        } else {
            log.error("Данные фильма внесены некорректно.");
            throw new ValidationException("Некорректные данные фильма");
        }
    }

    public Film updateFilm(Film film) {
        if (filmStorage.existsById(film.getId())) {
            filmStorage.putFilm(film);
            Film film1 = appointGenre(film);
            filmStorage.removeGenreFilm(film1.getId());
            for (Genre genre : film1.getGenres()) {
                filmStorage.addGenreToFilm(film1.getId(), genre.getId());
            }
            film1.getLikes().addAll(likeStorage.getLikeByIdFilm(film.getId()));
            log.info("Обновлены данные фильма: {}", film.getName());
            return film1;
        } else {
            log.error("Фильм не найден в списке ");
            throw new FilmNotFoundException(film.getId());
        }
    }


    public Film deleteFilm(Film film) {
        if (filmStorage.existsById(film.getId())) {
            filmStorage.deleteFilm(film);
            filmStorage.removeGenreFilm(film.getId());
        } else {
            log.error("Фильм не найден в списке");
            throw new FilmNotFoundException(film.getId());
        }
        log.info("Удален фильм: " + film.getName());
        return film;
    }

    public Optional<Film> findFilmById(String filmById) {
        long id = Validator.convertToLongFilm(filmById);
        if (filmStorage.existsById(id)) {
            Film film = filmStorage.findFilmById(id).get();
            film = new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), mpaStorage.findMpaById(film.getMpa().getId()).get());
            film.getGenres().addAll(filmStorage.getGenreFilmById(id));
            film.getLikes().addAll(likeStorage.getLikeByIdFilm(film.getId()));
            log.info("Фильм с id {}", film.getId());
            return Optional.of(film);
        } else {
            throw new FilmNotFoundException(id);
        }
    }

    public List<Film> sortFilmByLike(String count) {
        long size = Validator.convertToLongFilm(count);
        log.info("Список фильмов отсортирован по их популярности");
        return findAllFilms().stream().sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size()).limit(size).collect(Collectors.toList());
    }

    public Film addLikeFilm(String film, String user) {
        long filmById = Validator.convertToLongFilm(film);
        long userById = Validator.convertToLongFilm(user);
        Film film2 = filmStorage.findFilmById(filmById).get();
        if (likeStorage.isExist(filmById, userById)) {
            log.error("Пользователь уже оценил этот фильм лайком.");
            throw new WorkApplicationException("Пользователь уже оценил этот фильм лайком.");
        } else {
            likeStorage.addLike(filmById, userById);
            film2 = new Film(film2.getId(), film2.getName(), film2.getDescription(), film2.getReleaseDate(), film2.getDuration(), mpaStorage.findMpaById(film2.getMpa().getId()).get());
            film2.getGenres().addAll(filmStorage.getGenreFilmById(film2.getId()));
            film2.getLikes().addAll(likeStorage.getLikeByIdFilm(film2.getId()));
            log.info("Пользователь" + userById + "Оценил лайком №" + filmById);
            return film2;
        }
    }

    public Film removeLikeFilm(String film, String user) {
        long filmById = Validator.convertToLongFilm(film);
        long userById = Validator.convertToLongFilm(user);
        Film film1 = filmStorage.findFilmById(filmById).get();
        if (!likeStorage.isExist(filmById, userById)) {
            log.error("Пользователь не оценивал этот фильм.");
            throw new WorkApplicationException("Пользователь не оценивал этот фильм.");
        }
        likeStorage.removeLike(filmById, userById);
        log.info("Пользователь" + userById + " удалил свой лайк у фильма №" + filmById);
        return film1;
    }

    public Film appointGenre(Film film) {
        Film film1 = new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), mpaStorage.findMpaById(film.getMpa().getId()).get());
        film1.getGenres().addAll(film.getGenres());
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (film.getGenres().contains(genreStorage.findGenreById(genre.getId()).get())) {
                    film1.getGenres().remove(genreStorage.findGenreById(genre.getId()).get());
                    film1.getGenres().add(genreStorage.findGenreById(genre.getId()).get());
                } else {
                    film1.getGenres().add(genreStorage.findGenreById(genre.getId()).get());
                }
            }
        }
        return film1;
    }
}




