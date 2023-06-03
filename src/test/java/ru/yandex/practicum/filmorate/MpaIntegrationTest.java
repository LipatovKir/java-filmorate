package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class MpaIntegrationTest {

    private final MpaStorage mpaStorage;

    @Test
    void shouldFindMpaById() {
        final long id = 3L;
        final Optional<Mpa> mpaOptional = mpaStorage.findMpaById(id);
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa)
                                .hasFieldOrPropertyWithValue("id", id)
                                .hasFieldOrPropertyWithValue("name", "PG-13"));
    }

    @Test
    void shouldFindAllMpa() {
        final List<Mpa> mpaList = mpaStorage.findAllMpa();
        assertThat(mpaList).hasSize(5);
        Mpa mpaIdOne = mpaStorage.findMpaById(1L).get();
        Mpa mpaIdTwo = mpaStorage.findMpaById(2L).get();
        Mpa mpaIdThree = mpaStorage.findMpaById(3L).get();
        Mpa mpaIdFour = mpaStorage.findMpaById(4L).get();
        Mpa mpaIdFive = mpaStorage.findMpaById(5L).get();
        assertThat(mpaList.get(0)).isEqualTo(mpaIdOne);
        assertThat(mpaList.get(1)).isEqualTo(mpaIdTwo);
        assertThat(mpaList.get(2)).isEqualTo(mpaIdThree);
        assertThat(mpaList.get(3)).isEqualTo(mpaIdFour);
        assertThat(mpaList.get(4)).isEqualTo(mpaIdFive);
    }
}

