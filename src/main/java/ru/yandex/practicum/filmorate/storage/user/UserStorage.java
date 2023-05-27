package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    void putUser(User user);

    Optional<User> findUserById(Long id);

    boolean existsUserById(Long id);

    void deleteUser(User user);

    List<User> getAllUsers();
}
