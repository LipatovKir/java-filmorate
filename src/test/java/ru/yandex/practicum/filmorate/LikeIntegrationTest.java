package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class LikeIntegrationTest {

    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LocalDate testBirthday = LocalDate.of(1982, 10, 9);
    private final LocalDate correctReleaseDate = LocalDate.of(1895, Month.DECEMBER, 29);
    private final Film filmOne = new Film(null,
            "Новое кино1",
            "Описание нового фильма1",
            correctReleaseDate,
            100,
            new Mpa(1L));
    private final User userOne = new User(null,
            "test@yandex.ru",
            "Lipatov Kirill",
            "lipatovKIR",
            testBirthday);
    private final User userTwo = new User(null,
            "tests@yandex.ru",
            "Yandex Kirill",
            "yandexKIR",
            testBirthday);

    @AfterEach
    void afterEach() {
        filmStorage.getFilms().clear();
        userStorage.getAllUsers().clear();
    }

    @Test
    void shouldAddAndDeleteLikeFilm() {
        Film film = filmStorage.addFilm(filmOne);
        User userTest = userStorage.addUser(userOne);
        User userTestTwo = userStorage.addUser(userTwo);
        likeStorage.addLike(film.getId(), userTest.getId());
        likeStorage.addLike(film.getId(), userTestTwo.getId());
        List<Long> likeList = likeStorage.getLikeByIdFilm(film.getId());
        assertThat(likeList).hasSize(2);
        assertThat(likeList.get(0)).isEqualTo(userTest.getId());
        assertThat(likeList.get(1)).isEqualTo(userTestTwo.getId());
        likeStorage.removeLike(film.getId(), userTest.getId());
        List<Long> afterDellikeList = likeStorage.getLikeByIdFilm(film.getId());
        assertThat(afterDellikeList).hasSize(1);
        assertFalse(afterDellikeList.contains(userTest.getId()));
        assertThat(afterDellikeList.get(0)).isEqualTo(userTestTwo.getId());
    }
}
