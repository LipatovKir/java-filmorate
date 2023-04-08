package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public final Map<Integer, User> users = new HashMap<>();
    private static int id = 1;

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
        LocalDate currentTime = LocalDate.now();
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Электронная почта некорректна или не содержит символ @");
            throw new ValidationException("Электронная почта некорректна или не содержит символ @");
        } else if (user.getLogin() == null || user.getEmail().isBlank() || user.getLogin().contains(" ")) {
            log.info("Логин некорректен. ");
            throw new ValidationException("Логин некорректен. ");
        }  else if (user.getBirthday().isAfter(currentTime)) {
            log.info("Дата рождения некорректна. ");
            throw new ValidationException("Дата рождения некорректна. ");
        }
        if (user.getName() == null || user.getName().isBlank()) {
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
}
