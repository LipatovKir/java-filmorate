package ru.yandex.practicum.filmorate.service;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
@UtilityClass
public class Validator {

    private static final LocalDate startDate = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final LocalDate currentTime = LocalDate.now();
    private static final String FIRST_FILM_RELEASE =
            "Дата выпуска фильма не может быть раньше первого в истории человечества кинопоказа в Париже.";
    private static final String MUST_BE_NUMBER = "\" должно быть числом";

    public static boolean validateFilm(Film film) throws ValidationException {
        if (StringUtils.isBlank(film.getName())) {
            log.info("Нет названия фильма.");
            throw new ValidationException("Нет названия фильма.");
        } else if (StringUtils.isNotEmpty(film.getDescription()) && film.getDescription().length() > 200) {
            log.info("Описание фильма превышает 200 символов.");
            return false;
        } else if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(startDate)) {
            log.info(FIRST_FILM_RELEASE);
            throw new ValidationException(FIRST_FILM_RELEASE);
        } else if (film.getDuration() != null && film.getDuration() <= 0) {
            log.info("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        return true;
    }

    public static boolean validateReleaseDateFilm(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(startDate)) {
            log.error(FIRST_FILM_RELEASE);
            throw new ValidationException(FIRST_FILM_RELEASE);
        }
        return false;
    }

    public static void validateUser(User user) {
        if (StringUtils.isBlank(user.getEmail()) || !StringUtils.containsAny(user.getEmail(), "@")) {
            log.info("Электронная почта некорректна или не содержит символ @");
            throw new ValidationException("Электронная почта некорректна или не содержит символ @");
        } else if (user.getLogin() == null || user.getEmail().isBlank() || user.getLogin().contains(" ")) {
            log.info("Логин некорректен. ");
            throw new ValidationException("Логин некорректен. ");
        } else if (user.getBirthday().isAfter(currentTime)) {
            log.info("Дата рождения некорректна. ");
            throw new ValidationException("Дата рождения некорректна. ");
        }
    }

    public static Long convertToLongUser(String str) {
        long count;
        try {
            count = Long.parseLong(str);
        } catch (NumberFormatException e) {
            log.error("\"" + str + MUST_BE_NUMBER);
            throw new ValidationException("\"" + str + MUST_BE_NUMBER);
        }
        if (count <= 0) {
            log.error("\"" + str + "\" значение должно быть положительным");
            throw new UserNotFoundException(count);
        }
        return count;
    }

    public static Long convertToLongFilm(String str) {
        long count;
        try {
            count = Long.parseLong(str);
        } catch (NumberFormatException e) {
            log.error("\"" + str + MUST_BE_NUMBER);
            throw new ValidationException("\"" + str + MUST_BE_NUMBER);
        }
        if (count <= 0) {
            log.error("\"" + str + "\" значение должно быть положительным");
            throw new FilmNotFoundException(count);
        }
        return count;
    }
}
