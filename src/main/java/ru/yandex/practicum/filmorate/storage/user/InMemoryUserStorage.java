package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    public final Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getAllUsers() {
        return users;
    }

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User putUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delUser(User user) {
        users.remove(user.getId());
        return user;
    }
}
