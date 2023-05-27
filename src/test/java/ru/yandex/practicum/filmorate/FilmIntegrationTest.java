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
    final String newFilmOneName = "Новое кино1";
    final String newFilmOneDescription = "Описание нового фильма1";
    final String newFilmTwoName = "Новое кино2";
    final String newFilmTwoDescription = "Описание нового фильма2";
    static Film filmOne;
    static Film filmTwo;

    @BeforeEach
    void beforeEach() {
        filmOne = new Film(null, newFilmOneName, newFilmOneDescription,
                (LocalDate.of(2002, 5, 5)),
                100,
                new Mpa(2L));
        filmTwo = new Film(null, newFilmTwoName, newFilmTwoDescription,
                (LocalDate.of(2002, 3, 2)),
                90,
                new Mpa(5L));
    }

    @AfterEach
    void afterEach() {
        for (Film film : filmStorage.getFilms()) {
            filmStorage.deleteFilm(film);
        }
    }

    @Test
    void shouldCreateFilm() {
        final Film film = filmStorage.addFilm(filmOne);
        assertThat(film.getId()).isNotNull();
        assertThat(film.getName()).isEqualTo(filmOne.getName());
        assertThat(film.getDescription()).isEqualTo(filmOne.getDescription());
        assertThat(film.getReleaseDate()).isEqualTo(filmOne.getReleaseDate());
        assertThat(film.getDuration()).isEqualTo(filmOne.getDuration());
        assertThat(film.getMpa()).isEqualTo(filmOne.getMpa());
    }

    @Test
    void shouldNotCreateFilmWithEmptyName() {
        Film film = new Film(null, null, newFilmOneDescription, correctReleaseDate, 104, new Mpa(2L));
        try {
            filmStorage.addFilm(film);
            assertThrows(DataIntegrityViolationException.class, () -> {
            });
        } catch (DataIntegrityViolationException e) {
            System.out.println("Нет названия фильма.");
            assertTrue(filmStorage.getFilms().isEmpty());
        }
    }

    @Test
    void shouldNotCreateLongDescription() {
        Film film = new Film(null,
                newFilmOneName,
                "Описание фильма очень длинное описание фильма очень длинное " +
                        "Описание фильма очень длинное Описание фильма очень длинное Описание фильма очень длинное " +
                        "Описание фильма очень длинное Описание фильма очен ",
                correctReleaseDate,
                104,
                new Mpa(2L));
        try {
            filmStorage.addFilm(film);
            assertThrows(DataIntegrityViolationException.class, () -> {
            });
        } catch (DataIntegrityViolationException e) {
            System.out.println("Описание фильма превышает 200 символов.");
        }
        assertTrue(filmStorage.getFilms().isEmpty());
    }

    @Test
    void shouldCreateLong199Description() {
        Film film = new Film(null,
                newFilmOneName,
                "Описание фильма очень длинное описание фильма очень длинное " +
                        "Описание фильма очень длинное Описание фильма очень длинное Описание фильма очень длинное " +
                        "Описание фильма очень длинное Описание фильма ",
                correctReleaseDate,
                104,
                new Mpa(2L));
        filmStorage.addFilm(film);
        assertEquals(1, filmStorage.getFilms().size());
    }

    @Test
    void shouldNotCreateNotCorrectReleaseDate() {
        Film film = new Film(null, newFilmTwoName, newFilmTwoDescription, notCorrectReleaseDate, 113, new Mpa(10L));
        try {
            filmStorage.addFilm(film);
            assertThrows(DataIntegrityViolationException.class, () -> {
            });
        } catch (DataIntegrityViolationException e) {
            System.out.println("Дата выпуска фильма не может быть раньше " +
                    "первого в истории человечества кинопоказа в Париже.");
        }
        assertTrue(filmStorage.getFilms().isEmpty());
    }

    @Test
    void shouldNotCreateNegativeDuration() {
        Film film = new Film(null, newFilmTwoName, newFilmOneDescription, correctReleaseDate, -1, new Mpa(4L));
        try {
            filmStorage.addFilm(film);
            assertThrows(DataIntegrityViolationException.class, () -> {
            });
        } catch (DataIntegrityViolationException e) {
            System.out.println("Продолжительность фильма должна быть положительной");
            assertTrue(filmStorage.getFilms().isEmpty());
        }
    }

    @Test
    void testPutFilm() {
        final Film filmNew = filmStorage.addFilm(filmOne);
        final long id = filmNew.getId();
        Film filmThree = new Film(id,
                filmTwo.getName(),
                filmTwo.getDescription(),
                filmTwo.getReleaseDate(),
                filmTwo.getDuration(),
                filmTwo.getMpa());
        filmStorage.putFilm(filmThree);
        final Optional<Film> filmOptional = filmStorage.findFilmById(id);
        assertThat(filmOptional).isPresent().hasValueSatisfying(film ->
                assertThat(film).hasFieldOrPropertyWithValue("id", id)
                        .hasFieldOrPropertyWithValue("name", filmTwo.getName())
                        .hasFieldOrPropertyWithValue("description", filmTwo.getDescription())
                        .hasFieldOrPropertyWithValue("releaseDate", filmTwo.getReleaseDate())
                        .hasFieldOrPropertyWithValue("duration", filmTwo.getDuration())
                        .hasFieldOrPropertyWithValue("mpa", filmTwo.getMpa()));
    }

    @Test
    void shouldFindFilmById() {
        final long id = filmStorage.addFilm(filmOne).getId();
        final Optional<Film> filmOptional = filmStorage.findFilmById(id);
        assertThat(filmOptional).isPresent().hasValueSatisfying(film -> assertThat(film)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", filmOne.getName())
                .hasFieldOrPropertyWithValue("description", filmOne.getDescription())
                .hasFieldOrPropertyWithValue("releaseDate", filmOne.getReleaseDate())
                .hasFieldOrPropertyWithValue("duration", filmOne.getDuration())
                .hasFieldOrPropertyWithValue("mpa", filmOne.getMpa()));
    }

    @Test
    void shouldGetAllFilms() {
        filmStorage.addFilm(filmOne);
        filmStorage.addFilm(filmTwo);
        assertEquals(2, filmStorage.getFilms().size());
        final List<Film> allFilms = filmStorage.getFilms();
        assertThat(allFilms).hasSize(2).isNotNull();
    }

    @Test
    void shouldDeleteFilm() {
        final Film film = filmStorage.addFilm(filmOne);
        final long id = film.getId();
        filmStorage.deleteFilm(film);
        final Optional<Film> filmOptional = filmStorage.findFilmById(id);
        assertFalse(filmOptional.isPresent());
    }

    @Test
    void testGenreFilm() {
        final Film filmTest = filmStorage.addFilm(filmOne);
        final Genre genreOne = genreStorage.findGenreById(1L).get();
        final Genre genreTwo = genreStorage.findGenreById(1L).get();
        filmStorage.addGenreToFilm(filmTest.getId(), genreOne.getId());
        filmStorage.addGenreToFilm(filmTest.getId(), genreTwo.getId());
        List<Genre> genreList = filmStorage.getGenreFilmById(filmTest.getId());
        assertThat(genreList).hasSize(2);
        assertThat(genreList.get(0)).isEqualTo(genreOne);
        assertThat(genreList.get(1)).isEqualTo(genreTwo);
        filmStorage.removeGenreFilm(filmTest.getId());
        genreList = filmStorage.getGenreFilmById(filmTest.getId());
        assertTrue(genreList.isEmpty());
    }
}