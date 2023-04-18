package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    public final Map<Integer, User> users = new HashMap<>();
    private static int id = 1;
    private final LocalDate currentTime = LocalDate.now();

    private static int getId() {
        return id++;
    }

    public static void setId(int id) {
        UserController.id = id;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User  createUser(@Valid @RequestBody User user) {
        validateUser(user);
        if (StringUtils.isBlank(user.getName())) {
           log.debug("Имя не может быть пустым ли указано некорректно. Имя присвоено по умолчанию: {}", user.getLogin());
           user = new User(getId(), user.getEmail(), user.getLogin(), user.getLogin(), user.getBirthday());
        } else {
           user = new User(getId(), user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user.getLogin());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new ValidationException("Такого пользователя нет в списке. ");
        }
        log.info("Обновлены данные пользователя: {}", user.getLogin());
        return user;
    }

    private void validateUser(User user) {
        if (StringUtils.isBlank(user.getEmail()) || !StringUtils.containsAny(user.getEmail(), "@")) {
            log.info("Электронная почта некорректна или не содержит символ @");
            throw new ValidationException("Электронная почта некорректна или не содержит символ @");
        } else if (user.getLogin() == null || user.getEmail().isBlank() || user.getLogin().contains(" ")) {
            log.info("Логин некорректен. ");
            throw new ValidationException("Логин некорректен. ");
        }  else if (user.getBirthday().isAfter(currentTime)) {
            log.info("Дата рождения некорректна. ");
            throw new ValidationException("Дата рождения некорректна. ");
        }
        }
    }

