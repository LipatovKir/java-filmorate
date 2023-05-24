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

    public List<User> findAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers());
    }

    public User createUser(User user) {
        Validator.validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя не может быть пустым ли указано некорректно. Имя присвоено по умолчанию: {}", user.getLogin());
            user = new User(null, user.getEmail(), user.getLogin(), user.getLogin(), user.getBirthday());
        } else {
            user = new User(null, user.getEmail(), user.getName(), user.getLogin(), user.getBirthday());
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
                log.error("Пользователь № " + friendById + " Пользователь № " + userById + " уже друзья");
                throw new WorkApplicationException("Пользователь № " + friendById + " Пользователь № " + userById + " уже друзья");
            } else {
                friendsStorage.put(friendship);
                log.info("Пользователь № " + userById + " добавил в друзья пользователя № " + friendById);
            }
        } else {
            friendsStorage.add(friendship);
            log.info("Пользователь № " + friendById + " добавил в друзья пользователя № " + userById);
        }
        return findUserById(friend).get();
    }

    public User delFriends(String user1, String user2) {
        long friendById = Validator.convertToLongUser(user1);
        long userById = Validator.convertToLongUser(user2);
        Friendship friendship = new Friendship(friendById, userById);
        if (friendsStorage.isExist(friendship)) {
            log.error("Пользователь № " + friendById + " Пользователь № " + userById + " пока не друзья");
            friendsStorage.delete(friendship);
        } else {
            log.error("Пользователь №" + userById + " и пользователь №" + friendById + " не друзья.");
            throw new WorkApplicationException("Пользователь №" + userById + " и пользователь №" + friendById + " не друзья.");
        }
        return findUserById(user1).get();
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

    public List<User> mutualFriends(String user1, String user2) {
        long user1Id = Validator.convertToLongUser(user1);
        long user2Id = Validator.convertToLongUser(user2);
        Set<Long> friends = new HashSet<>(friendsStorage.getAllById(user1Id));
        friends.retainAll(friendsStorage.getAllById(user2Id));
        List<User> mutualFriends = new ArrayList<>();
        for (Long user : friends) {
            mutualFriends.add(userStorage.findUserById(user).get());
        }
        log.info("Список общих друзей пользователя № " + user1Id + " и пользователя № " + user2Id);
        return mutualFriends;
    }
}
/*




    @Override


    @Override
    public User addFriends(String user1, String user2) {

        long user1Id = parseStringInLong(user1);
        long user2Id = parseStringInLong(user2);

        Friendship friendship = new Friendship(user1Id, user2Id);

        if (friendshipStorage.isExist(friendship)) {

            if (friendshipStorage.status(friendship)) {
                throw new BusinessLogicException("User №" + user2Id + " and User №" + user1Id + " already friends");

            } else {
                friendshipStorage.put(friendship);
                log.info("User №" + user1Id + "and User №" + user2Id + "now friends");
            }

        } else {

            friendshipStorage.add(friendship);
            log.info("User №" + user2Id + "added to friends lists User №" + user1Id);
        }

        return findUserById(user1).get();
    }

    @Override
    public User delFriends(String user1, String user2) {

        long user1Id = parseStringInLong(user1);
        long user2Id = parseStringInLong(user2);

        Friendship friendship = new Friendship(user1Id, user2Id);

        if (friendshipStorage.isExist(friendship)) {
            log.info("User №" + user1Id + "and User №" + user2Id + " not friends anymore");
            friendshipStorage.del(friendship);

        } else {
            log.error("User №" + user2Id + " and User №" + user1Id + " not friends");
            throw new BusinessLogicException("User №" + user2Id + " and User №" + user1Id + " not friends");
        }

        return findUserById(user1).get();
    }

    @Override
    public List<User> friendsList(String user) {

        long userId = parseStringInLong(user);

        List<User> friendsList = new ArrayList<>();

        for (Long friend : friendshipStorage.findAllById(userId)) {
            friendsList.add(userStorage.findUserById(friend).get());
        }

        log.info("List friends User №" + userId);
        return friendsList;

    }

    @Override
    public List<User> commonFriends(String user1, String user2) {

        long user1Id = parseStringInLong(user1);
        long user2Id = parseStringInLong(user2);

        Set<Long> common = new HashSet<>(friendshipStorage.findAllById(user1Id));
        common.retainAll(friendshipStorage.findAllById(user2Id));

        List<User> commonFriends = new ArrayList<>();

        for (Long aLong : common) {
            commonFriends.add(userStorage.findUserById(aLong).get());
        }

        log.info("List of mutual friends User №" + user1Id + " and User №" + user2Id + "ready");
        return commonFriends;

    }

    public Long parseStringInLong(String str) {

        long a = 0;

        try {
            a = Long.parseLong(str);
        } catch (NumberFormatException e) {
            log.error("\"" + str + "\" must be a number");
            throw new ValidationException("\"" + str + "\" must be a number");
        }

        if (a <= 0) {
            log.error("\"" + str + "\" must be positive");
            throw new UserNotFoundException("\"" + str + "\" must be positive");
        }

        return a;
    }

}
 */