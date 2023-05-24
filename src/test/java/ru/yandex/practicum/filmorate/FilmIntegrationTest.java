package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class FilmIntegrationTest {

    final FilmStorage filmStorage;
    final GenreStorage genreStorage;
    final LocalDate correctReleaseDate = LocalDate.of(1895, Month.DECEMBER, 29);
    final LocalDate notCorrectReleaseDate = LocalDate.of(1895, Month.DECEMBER, 27);
    final String newFilm1Name = "Новое кино1";
    final String newFilm1Description = "Описание нового фильма1";
    final String newFilm2Name = "Новое кино2";
    final String newFilm2Description = "Описание нового фильма2";
    static Film film1;
    static Film film2;

    @BeforeEach
    void beforeEach() {
        film1 = new Film(null, newFilm1Name, newFilm1Description, (LocalDate.of(2002, 5, 5)), 100, new Mpa(2L));
        film2 = new Film(null, newFilm2Name, newFilm2Description, (LocalDate.of(2002, 3, 2)), 90, new Mpa(5L));
    }

    @AfterEach
    void afterEach() {
        for (Film film : filmStorage.getFilms()) {
            filmStorage.deleteFilm(film);
        }
    }

    @Test
    public void shouldCreateFilm() {
        final Film film = filmStorage.addFilm(film1);
        assertThat(film.getId()).isNotNull();
        assertThat(film.getName()).isEqualTo(film1.getName());
        assertThat(film.getDescription()).isEqualTo(film1.getDescription());
        assertThat(film.getReleaseDate()).isEqualTo(film1.getReleaseDate());
        assertThat(film.getDuration()).isEqualTo(film1.getDuration());
        assertThat(film.getMpa()).isEqualTo(film1.getMpa());
    }

    @Test
    public void shouldNotCreateFilmWithEmptyName() {
        Film film = new Film(null,null, newFilm1Description, correctReleaseDate, 104, new Mpa(2L));
        assertThrows(DataIntegrityViolationException.class, () -> {
            filmStorage.addFilm(film);
            throw new DataIntegrityViolationException("Нет названия фильма.");
        });
        assertTrue(filmStorage.getFilms().isEmpty());
    }

    @Test
    public void shouldNotCreateLongDescription() {
        Film film = new Film(null, newFilm1Name, "Описание фильма очень длинное описание фильма очень длинное " + "Описание фильма очень длинное Описание фильма очень длинное Описание фильма очень длинное " + "Описание фильма очень длинное Описание фильма очен ", correctReleaseDate, 104, new Mpa(2L));
        assertThrows(DataIntegrityViolationException.class, () -> {
            filmStorage.addFilm(film);
            throw new DataIntegrityViolationException("Описание фильма превышает 200 символов.");
        });
        assertTrue(filmStorage.getFilms().isEmpty());
    }

    @Test
    public void shouldCreateLong199Description() {
        Film film = new Film(null, newFilm1Name, "Описание фильма очень длинное описание фильма очень длинное " + "Описание фильма очень длинное Описание фильма очень длинное Описание фильма очень длинное " + "Описание фильма очень длинное Описание фильма ", correctReleaseDate, 104, new Mpa(2L));
        filmStorage.addFilm(film);
        assertEquals(1, filmStorage.getFilms().size());
    }

    @Test
    public void shouldNotCreateNotCorrectReleaseDate() {
        Film film = new Film(null, newFilm2Name, newFilm2Description, notCorrectReleaseDate, 113, new Mpa(10L));
        assertThrows(DataIntegrityViolationException.class, () -> {
            filmStorage.addFilm(film);
            throw new DataIntegrityViolationException("Дата выпуска фильма не может быть раньше первого в истории человечества кинопоказа в Париже.");
        });
        assertTrue(filmStorage.getFilms().isEmpty());
    }

    @Test
    public void shouldNotCreateNegativeDuration() {
        Film film = new Film(null, newFilm2Name, newFilm1Description, correctReleaseDate, -1, new Mpa(4L));
        assertThrows(DataIntegrityViolationException.class, () -> {
            filmStorage.addFilm(film);
            throw new DataIntegrityViolationException("Продолжительность фильма должна быть положительной");
        });
        assertTrue(filmStorage.getFilms().isEmpty());
    }

    @Test
    void testPutFilm() {
        final Film film11 = filmStorage.addFilm(film1);
        final long id = film11.getId();
        Film film3 = new Film(id, film2.getName(), film2.getDescription(), film2.getReleaseDate(), film2.getDuration(), film2.getMpa());
        filmStorage.putFilm(film3);
        final Optional<Film> filmOptional = filmStorage.findFilmById(id);
        assertThat(filmOptional).isPresent().hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", id).hasFieldOrPropertyWithValue("name", film2.getName()).hasFieldOrPropertyWithValue("description", film2.getDescription()).hasFieldOrPropertyWithValue("releaseDate", film2.getReleaseDate()).hasFieldOrPropertyWithValue("duration", film2.getDuration()).hasFieldOrPropertyWithValue("mpa", film2.getMpa()));
    }

    @Test
    void shouldFindFilmById() {
        final long id = filmStorage.addFilm(film1).getId();
        final Optional<Film> filmOptional = filmStorage.findFilmById(id);
        assertThat(filmOptional).isPresent().hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", id).hasFieldOrPropertyWithValue("name", film1.getName()).hasFieldOrPropertyWithValue("description", film1.getDescription()).hasFieldOrPropertyWithValue("releaseDate", film1.getReleaseDate()).hasFieldOrPropertyWithValue("duration", film1.getDuration()).hasFieldOrPropertyWithValue("mpa", film1.getMpa()));
    }

    @Test
    public void shouldGetAllFilms() {
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        assertEquals(2, filmStorage.getFilms().size());
        final List<Film> allFilms = filmStorage.getFilms();
        assertThat(allFilms.size()).isEqualTo(2);
        assertThat(allFilms).isNotNull();
    }

    @Test
    void shouldDeleteFilm() {
        final Film film = filmStorage.addFilm(film1);
        final long id = film.getId();
        filmStorage.deleteFilm(film);
        final Optional<Film> filmOptional = filmStorage.findFilmById(id);
        assertFalse(filmOptional.isPresent());
    }

    @Test
    void testGenreFilm() {
        final Film filmTest = filmStorage.addFilm(film1);
        final Genre genre1 = genreStorage.findGenreById(1L).get();
        final Genre genre2 = genreStorage.findGenreById(1L).get();
        filmStorage.addGenreToFilm(filmTest.getId(), genre1.getId());
        filmStorage.addGenreToFilm(filmTest.getId(), genre2.getId());
        List<Genre> genreList = filmStorage.getGenreFilmById(filmTest.getId());
        assertThat(genreList.size()).isEqualTo(2);
        assertThat(genreList.get(0)).isEqualTo(genre1);
        assertThat(genreList.get(1)).isEqualTo(genre2);
        filmStorage.removeGenreFilm(filmTest.getId());
        genreList = filmStorage.getGenreFilmById(filmTest.getId());
        assertTrue(genreList.isEmpty());
    }
}