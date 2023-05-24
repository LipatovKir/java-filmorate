package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.Mappers.FRIENDSHIP_MAPPER;

@Slf4j
@RequiredArgsConstructor
@Repository("FriendshipDbStorage")
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;
    final String SELECT_SECOND_USER_ID = "SELECT SECOND_USER_ID FROM FRIENDSHIP WHERE FIRST_USER_ID = ? " + "UNION SELECT FIRST_USER_ID FROM FRIENDSHIP WHERE SECOND_USER_ID = ? AND STATUS = TRUE";
    final String INSERT_INTO_FRIENDSHIP = "INSERT INTO FRIENDSHIP (FIRST_USER_ID,  SECOND_USER_ID) VALUES (?, ?)";
    final String UPDATE_SET_STATUS = "UPDATE FRIENDSHIP SET STATUS = ? WHERE FIRST_USER_ID = ? " + "and SECOND_USER_ID = ? OR FIRST_USER_ID = ? AND SECOND_USER_ID = ?";
    final String SELECT_FROM_FRIENDSHIP = "SELECT * FROM FRIENDSHIP WHERE FIRST_USER_ID = ? AND SECOND_USER_ID = ? " + "OR SECOND_USER_ID = ? AND FIRST_USER_ID = ?";
    final String DELETE_FROM_FRIENDSHIP = "DELETE FROM FRIENDSHIP WHERE FIRST_USER_ID = ? AND SECOND_USER_ID = ? " + "OR FIRST_USER_ID = ? AND SECOND_USER_ID = ?";
    final String SELECT_STATUS_FRIENDSHIP = "SELECT STATUS FROM FRIENDSHIP WHERE FIRST_USER_ID = ? AND SECOND_USER_ID = ? " + "OR FIRST_USER_ID = ? AND SECOND_USER_ID = ?";

    @Override
    public List<Long> getAllById(long id) {
        return jdbcTemplate.query(SELECT_SECOND_USER_ID, (rs, rowNum) -> rs.getLong("SECOND_USER_ID"), id, id);
    }

    @Override
    public void add(Friendship friendship) {
        jdbcTemplate.update(INSERT_INTO_FRIENDSHIP, friendship.getUser1ById(), friendship.getUser2ById());
    }

    @Override
    public void put(Friendship friendship) {
        jdbcTemplate.update(UPDATE_SET_STATUS, true, friendship.getUser1ById(), friendship.getUser2ById(), friendship.getUser2ById(), friendship.getUser1ById());
    }

    @Override
    public Optional<Friendship> findFriendship(Friendship friendship) {
        try {
            friendship = jdbcTemplate.queryForObject(SELECT_FROM_FRIENDSHIP, FRIENDSHIP_MAPPER, friendship.getUser1ById(), friendship.getUser2ById(), friendship.getUser2ById(), friendship.getUser1ById());
            assert friendship != null;
            return Optional.of(friendship);
        } catch (EmptyResultDataAccessException exception) {
            log.info("Пользователь id{} и пользователь id {} пока не друзья ", friendship.getUser1ById(), friendship.getUser2ById());
            return Optional.empty();
        }
    }

    @Override
    public void delete(Friendship friendship) {
        jdbcTemplate.update(DELETE_FROM_FRIENDSHIP, friendship.getUser1ById(), friendship.getUser2ById(), friendship.getUser2ById(), friendship.getUser1ById());
    }

    @Override
    public boolean status(Friendship friendship) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_STATUS_FRIENDSHIP, friendship.getUser1ById(), friendship.getUser2ById(), friendship.getUser2ById(), friendship.getUser1ById());
        if (userRows.next()) {
            return userRows.getBoolean("status");
        } else {
            return false;
        }
    }

    @Override
    public boolean isExist(Friendship friendship) {
        return findFriendship(friendship).isPresent();
    }
}

