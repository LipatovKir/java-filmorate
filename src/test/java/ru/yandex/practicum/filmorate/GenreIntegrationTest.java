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
class GenreIntegrationTest {

    private final GenreStorage genreStorage;

    @Test
    void shouldFindGenreById() {
        long id = 4L;
        Optional<Genre> genreOptional = genreStorage.findGenreById(id);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre)
                                .hasFieldOrPropertyWithValue("id", id)
                                .hasFieldOrPropertyWithValue("name", "Триллер"));
    }

    @Test
    void shouldFindAllGenres() {
        List<Genre> genreList = genreStorage.findAllGenres();
        assertThat(genreList).hasSize(6);
        Genre genreIdOne = genreStorage.findGenreById(1L).get();
        Genre genreIdTwo = genreStorage.findGenreById(2L).get();
        Genre genreIdThree = genreStorage.findGenreById(3L).get();
        assertThat(genreList.get(0)).isEqualTo(genreIdOne);
        assertThat(genreList.get(1)).isEqualTo(genreIdTwo);
        assertThat(genreList.get(2)).isEqualTo(genreIdThree);
    }
}
