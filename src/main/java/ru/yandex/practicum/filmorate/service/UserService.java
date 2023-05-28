package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        if (user.getName().isBlank()) {
            log.debug("Имя не может быть пустым ли указано некорректно. Имя присвоено по умолчанию: {}", user.getLogin());
            user = new User(user.getId(), user.getEmail(), user.getLogin(), user.getLogin(), user.getBirthday());
        } else {
            user = new User(user.getId(), user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
        }
        log.info("Добавлен новый пользователь: {}", user.getLogin());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.findUserById(user.getId()).isPresent()) {
            userStorage.putUser(user);
        } else {
            log.error("Пользователя нет в списке");
            throw new UserNotFoundException(user.getId());
        }
        log.info("Данные пользователя обновлены: {} ", user.getLogin());
        return user;
    }

    public User deleteUser(User user) {
        if (userStorage.findUserById(user.getId()).isPresent()) {
            userStorage.deleteUser(user);
        } else {
            log.error("Пользователя нет в списке");
            throw new UserNotFoundException(user.getId());
        }
        log.info("Пользователь удален: {}", user.getLogin());
        return user;
    }

    public Optional<User> findUserById(String userId) {
        long id;
        if (userId.isEmpty() || userId.isBlank()) {
            throw new ValidationException("ID пользователя не может быть пустым");
        }
        try {
            id = Validator.convertToLongUser(userId);
        } catch (NumberFormatException e) {
            throw new ValidationException("ID должно быть числом");
        }
        if (id <= 0) {
            throw new ValidationException("ID должно быть положительным");
        }
        if (userStorage.findUserById(id).isPresent()) {
            return userStorage.findUserById(id);
        } else {
            throw new UserNotFoundException(id);
        }
    }

    public User addFriends(String friend, String user) {
        long friendId = Validator.convertToLongUser(friend);
        long userId = Validator.convertToLongUser(user);
        Friendship friendship = new Friendship(friendId, userId);
        if (friendsStorage.findFriendship(friendship).isPresent()) {
            if (friendsStorage.status(friendship)) {
                log.error(USER_WITH_NUMBER + friendId + USER_WITH_NUMBER + userId + " уже друзья");
                throw new WorkApplicationException(USER_WITH_NUMBER + friendId + USER_WITH_NUMBER + userId + " уже друзья");
            } else {
                friendsStorage.put(friendship);
                log.info(USER_WITH_NUMBER + userId + " добавил в друзья пользователя № " + friendId);
            }
        } else {
            friendsStorage.add(friendship);
            log.info(USER_WITH_NUMBER + friendId + " добавил в друзья пользователя № " + userId);
        }
        return findUserById(friend).get();
    }

    public User delFriends(String userOne, String userTwo) {
        long friendId = Validator.convertToLongUser(userOne);
        long userId = Validator.convertToLongUser(userTwo);
        Friendship friendship = new Friendship(friendId, userId);
        if (friendsStorage.findFriendship(friendship).isPresent()) {
            log.error(USER_WITH_NUMBER + friendId + USER_WITH_NUMBER + userId + " пока не друзья");
            friendsStorage.delete(friendship);
        } else {
            log.error(USER_WITH_NUMBER + userId + " и " + USER_WITH_NUMBER + friendId + " не друзья.");
            throw new WorkApplicationException(USER_WITH_NUMBER + userId + " и " + USER_WITH_NUMBER + friendId + " не друзья.");
        }
        return findUserById(userOne).get();
    }

    public List<User> friendsList(String user) {
        long userId = Validator.convertToLongUser(user);
        List<User> friendsList = new ArrayList<>();
        for (Long friend : friendsStorage.getAllById(userId)) {
            friendsList.add(userStorage.findUserById(friend).get());
        }
        log.info("Список друзей пользователя № " + userId);
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
