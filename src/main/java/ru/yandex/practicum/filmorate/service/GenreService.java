package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> findAllGenres() {
        log.info("Список всех жанров ");
        return new ArrayList<>(genreStorage.findAllGenres());
    }

    public Genre findGenreById(long id) {
        Genre genre = genreStorage.findGenreById(id).get();
        if (genre != null ) {
            log.info("Жанр найден = {} ", genre.getId(), genre.getName());
            return genre;
        } else {
            throw new ObjectNotFoundException("Такой жанр не найден" + id);
        }
    }
}
