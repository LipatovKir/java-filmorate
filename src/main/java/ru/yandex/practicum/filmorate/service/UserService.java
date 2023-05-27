package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WorkApplicationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;
    private final FriendshipStorage friendsStorage;
    private static final String USER_WITH_NUMBER = "Пользователь № ";

    public List<User> findAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers());
    }

    public User createUser(User user) {
        Validator.validateUser(user);
        if (StringUtils.isBlank(user.getName())) {
            log.debug("Имя не может быть пустым ли указано некорректно. Имя присвоено по умолчанию: {}", user.getLogin());
            user = new User(user.getId(), user.getEmail(), user.getLogin(), user.getLogin(), user.getBirthday());
        } else {
            user = new User(user.getId(), user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
        }
        log.info("Добавлен новый пользователь: {}", user.getLogin());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.existsUserById(user.getId())) {
            userStorage.putUser(user);
        } else {
            log.error("Пользователя нет в списке");
            throw new UserNotFoundException(user.getId());
        }
        log.info("Данные пользователя обновлены: {} ", user.getLogin());
        return user;
    }

    public User deleteUser(User user) {
        if (userStorage.existsUserById(user.getId())) {
            userStorage.deleteUser(user);
        } else {
            log.error("Пользователя нет в списке");
            throw new UserNotFoundException(user.getId());
        }
        log.info("Пользователь удален: {}", user.getLogin());
        return user;
    }

    public Optional<User> findUserById(String userById) {
        long id;
        if (userById == null || userById.isBlank()) {
            throw new ValidationException("ID пользователя не может быть пустым");
        }
        try {
            id = Validator.convertToLongUser(userById);
        } catch (NumberFormatException e) {
            throw new ValidationException("ID должно быть числом");
        }
        if (id <= 0) {
            throw new ValidationException("ID должно быть положительным");
        }
        if (userStorage.existsUserById(id)) {
            return userStorage.findUserById(id);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    public User addFriends(String friend, String user) {
        long friendById = Validator.convertToLongUser(friend);
        long userById = Validator.convertToLongUser(user);
        Friendship friendship = new Friendship(friendById, userById);
        if (friendsStorage.isExist(friendship)) {
            if (friendsStorage.status(friendship)) {
                log.error(USER_WITH_NUMBER + friendById + USER_WITH_NUMBER + userById + " уже друзья");
                throw new WorkApplicationException(USER_WITH_NUMBER + friendById + USER_WITH_NUMBER + userById + " уже друзья");
            } else {
                friendsStorage.put(friendship);
                log.info(USER_WITH_NUMBER + userById + " добавил в друзья пользователя № " + friendById);
            }
        } else {
            friendsStorage.add(friendship);
            log.info(USER_WITH_NUMBER + friendById + " добавил в друзья пользователя № " + userById);
        }
        return findUserById(friend).get();
    }

    public User delFriends(String userOne, String userTwo) {
        long friendById = Validator.convertToLongUser(userOne);
        long userById = Validator.convertToLongUser(userTwo);
        Friendship friendship = new Friendship(friendById, userById);
        if (friendsStorage.isExist(friendship)) {
            log.error(USER_WITH_NUMBER + friendById + USER_WITH_NUMBER + userById + " пока не друзья");
            friendsStorage.delete(friendship);
        } else {
            log.error(USER_WITH_NUMBER + userById + " и " + USER_WITH_NUMBER + friendById + " не друзья.");
            throw new WorkApplicationException(USER_WITH_NUMBER + userById + " и " + USER_WITH_NUMBER + friendById + " не друзья.");
        }
        return findUserById(userOne).get();
    }

    public List<User> friendsList(String user) {
        long userById = Validator.convertToLongUser(user);
        List<User> friendsList = new ArrayList<>();
        for (Long friend : friendsStorage.getAllById(userById)) {
            friendsList.add(userStorage.findUserById(friend).get());
        }
        log.info("Список друзей пользователя № " + userById);
        return friendsList;
    }

    public List<User> mutualFriends(String userOne, String userTwo) {
        long userOneId = Validator.convertToLongUser(userOne);
        long userTwoId = Validator.convertToLongUser(userTwo);
        Set<Long> friends = new HashSet<>(friendsStorage.getAllById(userOneId));
        friends.retainAll(friendsStorage.getAllById(userTwoId));
        List<User> mutualFriends = new ArrayList<>();
        for (Long user : friends) {
            mutualFriends.add(userStorage.findUserById(user).get());
        }
        log.info("Список общих друзей пользователя № " + userOneId + " и пользователя № " + userTwoId);
        return mutualFriends;
    }
}
