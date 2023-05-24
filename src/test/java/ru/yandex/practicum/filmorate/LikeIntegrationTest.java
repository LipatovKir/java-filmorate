package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class LikeIntegrationTest {

    final LikeStorage likeStorage;
    final FilmStorage filmStorage;
    final UserStorage userStorage;
    static Film film1;
    static User user1;
    static User user2;
    static LocalDate testBirthday = LocalDate.of(1982, 10, 9);
    static LocalDate correctReleaseDate = LocalDate.of(1895, Month.DECEMBER, 29);

    @BeforeEach
    void beforeEach() {
        film1 = new Film(null, "Новое кино1", "Описание нового фильма1", correctReleaseDate, 100, new Mpa(1L));
        user1 = new User(null, "test@yandex.ru", "Lipatov Kirill", "lipatovKIR", testBirthday);
        user2 = new User(null, "tests@yandex.ru", "Yandex Kirill", "yandexKIR", testBirthday);
    }

    @AfterEach
    void afterEach() {
        filmStorage.getFilms().clear();
        userStorage.getAllUsers().clear();
        }

    @Test
    void shouldAddAndDeleteLikeFilm() {
        final Film film = filmStorage.addFilm(film1);
        final User user11 = userStorage.addUser(user1);
        final User user22 = userStorage.addUser(user2);
        likeStorage.addLike(film.getId(), user11.getId());
        likeStorage.addLike(film.getId(), user22.getId());
        final List<Long> likeList = likeStorage.getLikeByIdFilm(film.getId());
        assertThat(likeList.size()).isEqualTo(2);
        assertThat(likeList.get(0)).isEqualTo(user11.getId());
        assertThat(likeList.get(1)).isEqualTo(user22.getId());
        likeStorage.removeLike(film.getId(), user11.getId());
        final List<Long> afterDellikeList = likeStorage.getLikeByIdFilm(film.getId());
        assertThat(afterDellikeList.size()).isEqualTo(1);
        assertFalse(afterDellikeList.contains(user11.getId()));
        assertThat(afterDellikeList.get(0)).isEqualTo(user22.getId());
    }
}