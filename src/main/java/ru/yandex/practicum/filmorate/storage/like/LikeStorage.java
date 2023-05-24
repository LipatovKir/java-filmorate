package ru.yandex.practicum.filmorate.storage.like;

import java.util.List;

public interface LikeStorage {

    void addLike(Long filmById, Long userById);

    void removeLike(Long filmById, Long userById);

    List<Long> getLikeByIdFilm(Long filmById);

    boolean isExist(Long filmById, Long userById);
}

