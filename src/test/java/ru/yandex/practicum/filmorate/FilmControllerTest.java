package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private static FilmController filmController;
    private final LocalDate correctReleaseDate = LocalDate.of(1895, Month.DECEMBER, 29);
    private final LocalDate notCorrectReleaseDate = LocalDate.of(1895, Month.DECEMBER, 27);
    private final String newFilmName = "Новое кино";
    private final String newFilmDescription = "Описание нового фильма";

    @BeforeEach
    public void beforeEach() {
        final FilmService filmService = new FilmService(new InMemoryFilmStorage());
        filmController = new FilmController(filmService);
    }

    @Test
    public void shouldCreateFilm() {
        Film film = new Film(1L, newFilmName, newFilmDescription, correctReleaseDate, 136);
        filmController.create(film);
        assertFalse(filmController.findAll().isEmpty());
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    public void shouldNotCreateFilmWithEmptyName() {
        Film film = new Film(null, "  ", newFilmDescription, correctReleaseDate, 89);
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
            throw new ValidationException("Нет названия фильма.");
        });
        assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    public void shouldNotCreateLongDescription() {
        Film film = new Film(null, newFilmName, "Описание фильма очень длинное описание фильма очень длинное " + "Описание фильма очень длинное Описание фильма очень длинное Описание фильма очень длинное " + "Описание фильма очень длинное Описание фильма очен ", correctReleaseDate, 104);
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
            throw new ValidationException("Описание фильма превышает 200 символов.");
        });
        assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    public void shouldCreateLong199Description() {
        Film film = new Film(null, newFilmName, "Описание фильма очень длинное описание фильма очень длинное " + "Описание фильма очень длинное Описание фильма очень длинное Описание фильма очень длинное " + "Описание фильма очень длинное Описание фильма ", correctReleaseDate, 104);
        filmController.create(film);
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    public void shouldNotCreateNotCorrectReleaseDate() {
        Film film = new Film(null, newFilmName, newFilmDescription, notCorrectReleaseDate, 113);
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
            throw new ValidationException("Дата выпуска фильма не может быть раньше первого в истории человечества кинопоказа в Париже.");
        });
        assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    public void shouldNotCreateNegativeDuration() {
        Film film = new Film(null, newFilmName, newFilmDescription, correctReleaseDate, -1);
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        });
        assertTrue(filmController.findAll().isEmpty());
    }

    @Test
    public void shouldGetAllFilms() {
        Film film1 = new Film(1L, newFilmName, newFilmDescription, correctReleaseDate, 1);
        filmController.create(film1);
        Film film2 = new Film(2L, newFilmName, newFilmDescription, correctReleaseDate, 3);
        filmController.create(film2);
        assertEquals(2, filmController.findAll().size());
    }
}

