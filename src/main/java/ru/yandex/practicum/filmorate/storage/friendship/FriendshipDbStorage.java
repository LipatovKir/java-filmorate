package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.Mappers.FRIENDSHIP_MAPPER;

@Slf4j
@RequiredArgsConstructor
@Repository("FriendshipDbStorage")
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_SECOND_USER =
            "SELECT SECOND_USER_ID FROM FRIENDSHIP WHERE FIRST_USER_ID = ? " +
                    "UNION SELECT FIRST_USER_ID FROM FRIENDSHIP WHERE SECOND_USER_ID = ? AND STATUS = TRUE";
    private static final String INSERT_INTO_FRIENDSHIP_FIRST_USER_ID_SECOND_USER_ID_VALUES =
            "INSERT INTO FRIENDSHIP (FIRST_USER_ID,  SECOND_USER_ID) VALUES (?, ?)";
    private static final String UPDATE_SET_STATUS =
            "UPDATE FRIENDSHIP SET STATUS = ? WHERE FIRST_USER_ID = ? " +
                    "and SECOND_USER_ID = ? OR FIRST_USER_ID = ? AND SECOND_USER_ID = ?";
    private static final String SELECT_FROM_FRIENDSHIP =
            "SELECT * FROM FRIENDSHIP WHERE FIRST_USER_ID = ? AND SECOND_USER_ID = ? " +
                    "OR SECOND_USER_ID = ? AND FIRST_USER_ID = ?";
    private static final String DELETE_FROM_FRIENDSHIP =
            "DELETE FROM FRIENDSHIP WHERE FIRST_USER_ID = ? AND SECOND_USER_ID = ? " +
                    "OR FIRST_USER_ID = ? AND SECOND_USER_ID = ?";
    private static final String SELECT_STATUS_FRIENDSHIP =
            "SELECT STATUS FROM FRIENDSHIP WHERE FIRST_USER_ID = ? AND SECOND_USER_ID = ? " +
                    "OR FIRST_USER_ID = ? AND SECOND_USER_ID = ?";

    @Override
    public List<Long> getAllById(long id) {
        return jdbcTemplate.query(SELECT_SECOND_USER, (rs, rowNum) -> rs.getLong("SECOND_USER_ID"), id, id);
    }

    @Override
    public void add(Friendship friendship) {
        jdbcTemplate.update(INSERT_INTO_FRIENDSHIP_FIRST_USER_ID_SECOND_USER_ID_VALUES,
                friendship.getUserId(),
                friendship.getFriendId());
    }

    @Override
    public void put(Friendship friendship) {
        jdbcTemplate.update(UPDATE_SET_STATUS, true,
                friendship.getUserId(),
                friendship.getFriendId(),
                friendship.getFriendId(),
                friendship.getUserId());
    }

    @Override
    public Optional<Friendship> findFriendship(Friendship friendship) {
        try {
            friendship = jdbcTemplate.queryForObject(SELECT_FROM_FRIENDSHIP, FRIENDSHIP_MAPPER,
                    friendship.getUserId(),
                    friendship.getFriendId(),
                    friendship.getFriendId(),
                    friendship.getUserId());
            checkFriendship(friendship);
            return Optional.ofNullable(friendship);
        } catch (EmptyResultDataAccessException exception) {
            log.info("Пользователь id{} и пользователь id {} пока не друзья ",
                    friendship.getUserId(),
                    friendship.getFriendId());
            return Optional.empty();
        }
    }

    @Override
    public void delete(Friendship friendship) {
        jdbcTemplate.update(DELETE_FROM_FRIENDSHIP,
                friendship.getUserId(),
                friendship.getFriendId(),
                friendship.getFriendId(),
                friendship.getUserId());
    }

    @Override
    public boolean status(Friendship friendship) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_STATUS_FRIENDSHIP,
                friendship.getUserId(),
                friendship.getFriendId(),
                friendship.getFriendId(),
                friendship.getUserId());
        if (userRows.next()) {
            return userRows.getBoolean("status");
        } else {
            return false;
        }
    }

    public void checkFriendship(Friendship friendship) {
        if (friendship == null) {
            throw new ObjectNotFoundException("Друзья не найдены.");
        }
    }
}

