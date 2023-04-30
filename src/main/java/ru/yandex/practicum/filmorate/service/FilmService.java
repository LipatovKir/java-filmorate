package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WorkApplicationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private long id = 1;

    private long getId() {
        return id++;
    }

    public List<Film> findAllFilms() {
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    public Film createFilm(Film film) {
        if (Validator.validateFilm(film) && !Validator.validateReleaseDateFilm(film)) {
            film = new Film(getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
            log.info("Добавлен новый фильм: {}", film.getName());
            return filmStorage.addFilm(film);
        } else {
            log.error("Данные фильма внесены некорректно.");
            throw new ValidationException("Некорректные данные фильма");
        }
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilms().containsKey(film.getId())) {
            filmStorage.putFilm(film);
        } else {
            log.error("Фильм не найден в списке ");
            throw new FilmNotFoundException(film.getId());
        }
        log.info("Обновлены данные фильма: {}", film.getName());
        return film;
    }

    public Film deleteFilm(Film film) {
        if (filmStorage.getFilms().containsKey(film.getId())) {
            filmStorage.deleteFilm(film);
        } else {
            log.error("Фильм не найден в списке");
            throw new FilmNotFoundException(film.getId());
        }
        log.info("Удален фильм: " + film.getName());
        return film;
    }

    public Film findFilmById(String filmId) {
        long id = Validator.convertToLongFilm(filmId);
        if (filmStorage.getFilms().containsKey(id)) {
            return filmStorage.getFilms().get(id);
        } else {
            throw new FilmNotFoundException(id);
        }
    }

    public List<Film> sortFilmByLike(String count) {
        long size = Validator.convertToLongFilm(count);
        log.info("Cписок фильмов отсортирован по их популярности");
        return filmStorage.getFilms().values()
                .stream()
                .sorted((film1, film2) -> film2.getLikes().size()
                        - film1.getLikes().size())
                .limit(size)
                .collect(Collectors.toList());
    }

    public Film addLikeFilm(String film, String user) {
        long filmById = Validator.convertToLongFilm(film);
        long userById = Validator.convertToLongFilm(user);
        if (filmStorage.getFilms().get(filmById).getLikes().contains(userById)) {
            log.error("Пользователь уже оценил этот фильм лайком.");
            throw new WorkApplicationException("Пользователь уже оценил этот фильм лайком.");
        }
        findFilmById(film).getLikes().add(userById);
        log.info("Пользователь" + userById + "Оценил лайком №" + filmById);
        return filmStorage.getFilms().get(filmById);
    }

    public Film removeLikeFilm(String film, String user) {
        long filmById = Validator.convertToLongFilm(film);
        long userById = Validator.convertToLongFilm(user);
        if (!filmStorage.getFilms().get(filmById).getLikes().contains(userById)) {
            log.error("Пользователь не оценивал этот фильм.");
            throw new WorkApplicationException("Пользователь не оценивал этот фильм.");
        }
        findFilmById(film).getLikes().remove(userById);
        log.info("Пользователь" + userById + " удалил свой лайк у фильма №" + filmById);
        return filmStorage.getFilms().get(filmById);
    }
}

