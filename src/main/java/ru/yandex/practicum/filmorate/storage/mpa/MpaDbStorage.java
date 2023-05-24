package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.Mappers.MPA_MAPPER;

@Slf4j
@RequiredArgsConstructor
@Repository("MpaStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    final String SELECT_FROM_MPA = "SELECT * FROM MPA WHERE MPA_ID = ?";
    final String SELECT_FROM_MPA_ORDER = "SELECT * FROM MPA ORDER BY MPA_ID";

    @Override
    public Optional<Mpa> findMpaById(long id) {
        try {
            Mpa mpa = jdbcTemplate.queryForObject(SELECT_FROM_MPA, MPA_MAPPER, id);
            assert mpa != null;
            log.info("Рейтинг-MРА id {} название {}", mpa.getId(), mpa.getName());
            return Optional.of(mpa);
        } catch (EmptyResultDataAccessException exception) {
            log.info("Рейтинг-MРА id " + id + " не найден.");
            throw new ObjectNotFoundException("Рейтинг-MРА id " + id + " не найден.");
        }
    }

    @Override
    public List<Mpa> findAllMpa() {
        return jdbcTemplate.query(SELECT_FROM_MPA_ORDER, MPA_MAPPER);
    }
}
