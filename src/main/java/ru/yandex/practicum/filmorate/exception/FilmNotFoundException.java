package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(long id) {
        super(String.format("Не найден фильм с id: %s", id));
    }
}