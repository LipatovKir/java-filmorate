package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class GenreIntegrationTest {

    final GenreStorage genreStorage;

    @Test
    void shouldFindGenreById() {
        final long id = 4L;
        final Optional<Genre> genreOptional = genreStorage.findGenreById(id);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre)
                                .hasFieldOrPropertyWithValue("id", id)
                                .hasFieldOrPropertyWithValue("name", "Триллер"));
    }

    @Test
    void shouldFindAllGenres() {
        final List<Genre> genreList = genreStorage.findAllGenres();
        assertThat(genreList.size()).isEqualTo(6);
        Genre genreId1 = genreStorage.findGenreById(1L).get();
        Genre genreId2 = genreStorage.findGenreById(2L).get();
        Genre genreId3 = genreStorage.findGenreById(3L).get();
        assertThat(genreList.get(0)).isEqualTo(genreId1);
        assertThat(genreList.get(1)).isEqualTo(genreId2);
        assertThat(genreList.get(2)).isEqualTo(genreId3);
    }
}
