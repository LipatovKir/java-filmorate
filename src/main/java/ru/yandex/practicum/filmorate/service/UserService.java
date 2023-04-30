package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private long id = 1;

    private long getId() {
        return id++;
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers().values());
    }

    public User createUser(User user) {
        Validator.validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя не может быть пустым ли указано некорректно. Имя присвоено по умолчанию: {}", user.getLogin());
            user = new User(getId(), user.getEmail(), user.getLogin(), user.getLogin(), user.getBirthday());
        } else {
            user = new User(getId(), user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
        }
        log.info("Добавлен новый пользователь: {}", user.getLogin());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.getAllUsers().containsKey(user.getId())) {
            userStorage.putUser(user);
        } else {
            log.error("Пользователя нет в списке");
            throw new UserNotFoundException(user.getId());
        }
        log.info("Данные пользователя обновлены: {} ", user.getLogin());
        return user;
    }

    public User deleteUser(User user) {
        if (userStorage.getAllUsers().containsKey(user.getId())) {
            userStorage.delUser(user);
        } else {
            log.error("Пользователя нет в списке");
            throw new UserNotFoundException(Math.toIntExact(user.getId()));
        }
        log.info("Пользователь удален: {}", user.getLogin());
        return user;
    }

    public User findUserById(String userById) {
        long id;
        if (userById == null || userById.isBlank()) {
            throw new ValidationException("ID пользователя не может быть пустым");
        }
        try {
            id = Long.parseLong(userById);
        } catch (NumberFormatException e) {
            throw new ValidationException("ID должно быть числом");
        }
        if (id <= 0) {
            throw new ValidationException("ID должно быть положительным");
        }
        if (userStorage.getAllUsers().containsKey(id)) {
            return userStorage.getAllUsers().get(id);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    public User addFriends(String friends, String users) {
        long friendId = Validator.convertToLongUser(friends);
        long userId = Validator.convertToLongUser(users);
        if (userStorage.getAllUsers().get(friendId).getFriends().contains(userId)) {
            log.error("Пользователь № " + friendId + " Пользователь № " + userId + " уже друзья");
            throw new ValidationException("Пользователь № " + friendId + " Пользователь № " + userId + " уже друзья");
        }
        findUserById(friends).getFriends().add(userId);
        findUserById(users).getFriends().add(friendId);
        log.info("Пользователь № " + userId + " добавил в друзья пользователя № " + friendId);
        return userStorage.getAllUsers().get(friendId);
    }

    public User delFriends(String user1, String user2) {
        long friendId = Validator.convertToLongUser(user1);
        long userId = Validator.convertToLongUser(user2);
        if (!userStorage.getAllUsers().get(friendId).getFriends().contains(user2)) {
            log.error("Пользователь № " + friendId + " Пользователь № " + userId + " пока не друзья");
        }
        log.info("Пользователь №" + userId + " удалил из своего списка друзей пользователя №" + friendId);
        findUserById(user1).getFriends().remove(userId);
        log.info("Пользователь №" + userId + " удалил из своего списка друзей пользователя №" + friendId);
        findUserById(user2).getFriends().remove(friendId);
        log.info("Пользователь № " + friendId + " Пользователь № " + userId + " больше не друзья");
        return userStorage.getAllUsers().get(friendId);
    }

    public List<User> friendsList(String user) {
        long userId = Validator.convertToLongUser(user);
        List<User> friendsList = new ArrayList<>();
        for (Long friend : userStorage.getAllUsers().get(userId).getFriends()) {
            friendsList.add(userStorage.getAllUsers().get(friend));
        }
        log.info("Список друзей пользователя № " + userId);
        return friendsList;
    }

    public List<User> mutualFriends(String user1, String user2) {
        long user1Id = Validator.convertToLongUser(user1);
        long user2Id = Validator.convertToLongUser(user2);
        List<User> mutualFriends = new ArrayList<>();
        for (Long friend : userStorage.getAllUsers().get(user1Id).getFriends()) {
            if (userStorage.getAllUsers().get(user2Id).getFriends().contains(friend)) {
                mutualFriends.add(userStorage.getAllUsers().get(friend));
            }
        }
        log.info("Список общих друзей пользователя № " + user1Id + " и пользователя № " + user2Id);
        return mutualFriends;
    }
}


