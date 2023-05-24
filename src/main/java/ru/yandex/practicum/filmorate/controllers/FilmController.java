package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;

import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;


    @GetMapping
    public Collection<Film> findAllFilms() {
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
    public Film delete(@RequestBody Film film) {
        return filmService.deleteFilm(film);
    }

    @GetMapping("/{id}")
    public Optional<Film> findFilmById(@PathVariable("id") String id) {
        return filmService.findFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") String id,
                        @PathVariable("userId") String userById) {
        return filmService.addLikeFilm(id, userById);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") String id,
                           @PathVariable("userId") String userById) {
        return filmService.removeLikeFilm(id, userById);
    }

    @GetMapping("/popular")
    public List<Film> popularFilmList(@RequestParam(defaultValue = "10") String count) {
        return filmService.sortFilmByLike(count);
    }
}
