package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate startDate = LocalDate.of(1895, Month.DECEMBER, 28);
    public final Map<Integer, Film> films = new HashMap<>();
    private static int id = 1;

    private static int getId() {
        return id++;
    }

    public static void setId(int id) {
        FilmController.id = id;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (!validateFilm(film)) {
            if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(startDate)) {
                log.error("Дата выпуска фильма не может быть раньше первого в истории человечества кинопоказа в Париже.");
                throw new ValidationException("Дата выпуска фильма не может быть раньше первого в истории человечества кинопоказа в Париже.");
            }
            film = new Film(getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм: {}", film.getName());
            return film;
        } else {
            log.error("Данные фильма внесены некорректно.");
            throw new ValidationException("Некорректные данные фильма");
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new ValidationException("Такого фильма нет в списке. ");
        }
        log.info("Обновлено описание фильма: {}", film.getName());
        films.put(film.getId(), film);
        return film;
    }

    private boolean validateFilm(Film film) {
        if (StringUtils.isBlank(film.getName())) {
            log.info("Нет названия фильма.");
            throw new ValidationException("Нет названия фильма.");
        } else if (StringUtils.isNotEmpty(film.getDescription()) && film.getDescription().length() > 200) {
            log.info("Описание фильма превышает 200 символов.");
            return true;
        } else if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(startDate)) {
            log.info("Дата выпуска фильма не может быть раньше первого в истории человечества кинопоказа в Париже.");
            throw new ValidationException("Дата выпуска фильма не может быть раньше первого в истории человечества кинопоказа в Париже.");
        } else if (film.getDuration() != null && film.getDuration() <= 0) {
            log.info("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        return false;
    }
}
