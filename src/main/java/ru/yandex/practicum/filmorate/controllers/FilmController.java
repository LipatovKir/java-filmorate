package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAllFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping
    public Film del(@RequestBody Film film) {
        return filmService.deleteFilm(film);
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") String id) {
        return filmService.findFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") String id,
                        @PathVariable("userId") String userId) {
        return filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film delLike(@PathVariable("id") String id,
                        @PathVariable("userId") String userId) {
        return filmService.removeLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> popularFilmList(@RequestParam(defaultValue = "10") String count) {
        return filmService.sortFilmByLike(count);
    }
}
