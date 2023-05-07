package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super(String.format("Не найден пользователь с id: %s", id));
    }
}
