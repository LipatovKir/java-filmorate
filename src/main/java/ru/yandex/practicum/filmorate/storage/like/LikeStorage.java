package ru.yandex.practicum.filmorate.storage.like;

import java.util.List;

public interface LikeStorage {

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Long> getLikeByIdFilm(Long filmId);

    boolean isExist(Long filmId, Long userId);
}

