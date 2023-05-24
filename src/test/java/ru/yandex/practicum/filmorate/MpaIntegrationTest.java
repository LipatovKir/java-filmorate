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
public class MpaIntegrationTest {

    final MpaStorage mpaStorage;

    @Test
    void shouldFindMpaById() {
        final long id = 3L;
        final Optional<Mpa> mpaOptional = mpaStorage.findMpaById(id);
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", id)
                                .hasFieldOrPropertyWithValue("name", "PG-13"));
    }

    @Test
    void shouldFindAllMpa() {
        final List<Mpa> mpaList = mpaStorage.findAllMpa();
        assertThat(mpaList.size()).isEqualTo(5);
        Mpa mpaId1 = mpaStorage.findMpaById(1L).get();
        Mpa mpaId2 = mpaStorage.findMpaById(2L).get();
        Mpa mpaId3 = mpaStorage.findMpaById(3L).get();
        Mpa mpaId4 = mpaStorage.findMpaById(4L).get();
        Mpa mpaId5 = mpaStorage.findMpaById(5L).get();
        assertThat(mpaList.get(0)).isEqualTo(mpaId1);
        assertThat(mpaList.get(1)).isEqualTo(mpaId2);
        assertThat(mpaList.get(2)).isEqualTo(mpaId3);
        assertThat(mpaList.get(3)).isEqualTo(mpaId4);
        assertThat(mpaList.get(4)).isEqualTo(mpaId5);
    }
}

