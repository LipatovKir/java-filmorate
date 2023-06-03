package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipStorage {

    List<Long> getAllById(long id);

    void add(Friendship friendship);

    void put(Friendship friendship);

    Optional<Friendship> findFriendship(Friendship friendship);

    void delete(Friendship friendship);

    boolean status(Friendship friendship);
}
