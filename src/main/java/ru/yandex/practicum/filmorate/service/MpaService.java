package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> findAllMpa() {
        log.info("Весь список рейтингов-МРА");
        return new ArrayList<>(mpaStorage.findAllMpa());
    }

    public Mpa findMpaById(long id) {
        Optional<Mpa> optMpa = mpaStorage.findMpaById(id);
        if (optMpa.isPresent()) {
            Mpa mpa = optMpa.get();
            log.info("Рейтинг-MРА id {}, название {}", mpa.getId(), mpa.getName());
            return mpa;
        } else {
            throw new ObjectNotFoundException("Рейтинг-MРА id " + id + " не найден.");
        }
    }
}
