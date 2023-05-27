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
            Film filmNew = new Film(film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    mpaStorage.findMpaById(film.getMpa().getId()).get());
            filmNew.getGenres().addAll(filmStorage.getGenreFilmById(filmNew.getId()));
            filmNew.getLikes().addAll(likeStorage.getLikeByIdFilm(film.getId()));
            films.add(filmNew);
        }
        log.info("Список всех фильмов ");
        return films;
    }

    public Film createFilm(Film film) {
        if (Validator.validateFilm(film) && !Validator.validateReleaseDateFilm(film)) {
            Film filmNew = appointGenre(film);
            log.info("Добавлен новый фильм: {}", film.getName());
            return filmStorage.addFilm(filmNew);
        } else {
            log.error("Данные фильма внесены некорректно.");
            throw new ValidationException("Некорректные данные фильма");
        }
    }

    public Film updateFilm(Film film) {
        if (filmStorage.existsById(film.getId())) {
            filmStorage.putFilm(film);
            Film filmNew = appointGenre(film);
            filmStorage.removeGenreFilm(filmNew.getId());
            for (Genre genre : filmNew.getGenres()) {
                if (!filmStorage.getGenreFilmById(filmNew.getId()).contains(genre)) {
                    filmStorage.addGenreToFilm(filmNew.getId(), genre.getId());
                }
            }
            filmNew.getLikes().addAll(likeStorage.getLikeByIdFilm(film.getId()));
            log.info("Обновлены данные фильма: {}", film.getName());
            return filmNew;
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
        Optional<Film> optFilm = filmStorage.findFilmById(id);
        if (optFilm.isPresent()) {
            Film film = optFilm.get();
            film = new Film(film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    mpaStorage.findMpaById(film.getMpa().getId()).get());
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
        return findAllFilms().stream().sorted((filmOne, filmTwo) ->
                filmTwo.getLikes().size() - filmOne.getLikes().size()).limit(size).collect(Collectors.toList());
    }

    public Film addLikeFilm(String film, String user) {
        long filmById = Validator.convertToLongFilm(film);
        long userById = Validator.convertToLongFilm(user);
        Film filmTwo = filmStorage.findFilmById(filmById).get();
        if (likeStorage.isExist(filmById, userById)) {
            log.error("Пользователь уже оценил этот фильм лайком.");
            throw new WorkApplicationException("Пользователь уже оценил этот фильм лайком.");
        } else {
            likeStorage.addLike(filmById, userById);
            filmTwo = new Film(filmTwo.getId(),
                    filmTwo.getName(),
                    filmTwo.getDescription(),
                    filmTwo.getReleaseDate(),
                    filmTwo.getDuration(),
                    mpaStorage.findMpaById(filmTwo.getMpa().getId()).get());
            filmTwo.getGenres().addAll(filmStorage.getGenreFilmById(filmTwo.getId()));
            filmTwo.getLikes().addAll(likeStorage.getLikeByIdFilm(filmTwo.getId()));
            log.info("Пользователь" + userById + "Оценил лайком №" + filmById);
            return filmTwo;
        }
    }

    public Film removeLikeFilm(String film, String user) {
        long filmById = Validator.convertToLongFilm(film);
        long userById = Validator.convertToLongFilm(user);
        Film filmOne = filmStorage.findFilmById(filmById).get();
        if (!likeStorage.isExist(filmById, userById)) {
            log.error("Пользователь не оценивал этот фильм.");
            throw new WorkApplicationException("Пользователь не оценивал этот фильм.");
        }
        likeStorage.removeLike(filmById, userById);
        log.info("Пользователь" + userById + " удалил свой лайк у фильма №" + filmById);
        return filmOne;
    }

    public Film appointGenre(Film film) {
        Film filmNew = new Film(film.getId(),
                film.getName(), film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaStorage.findMpaById(film.getMpa().getId()).get());
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                film.getGenres().contains(genreStorage.findGenreById(genre.getId()).get());
                filmNew.getGenres().remove(genreStorage.findGenreById(genre.getId()).get());
                filmNew.getGenres().add(genreStorage.findGenreById(genre.getId()).get());
            }
        }
        return filmNew;
    }
}

