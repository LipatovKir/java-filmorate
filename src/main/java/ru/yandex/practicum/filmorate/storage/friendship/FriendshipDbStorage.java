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
    final String selectSecondUser = "SELECT SECOND_USER_ID FROM FRIENDSHIP WHERE FIRST_USER_ID = ? " + "UNION SELECT FIRST_USER_ID FROM FRIENDSHIP WHERE SECOND_USER_ID = ? AND STATUS = TRUE";
    final String insertIntoFriendship = "INSERT INTO FRIENDSHIP (FIRST_USER_ID,  SECOND_USER_ID) VALUES (?, ?)";
    final String updateSetStatus = "UPDATE FRIENDSHIP SET STATUS = ? WHERE FIRST_USER_ID = ? " + "and SECOND_USER_ID = ? OR FIRST_USER_ID = ? AND SECOND_USER_ID = ?";
    final String selectFromFriendship = "SELECT * FROM FRIENDSHIP WHERE FIRST_USER_ID = ? AND SECOND_USER_ID = ? " + "OR SECOND_USER_ID = ? AND FIRST_USER_ID = ?";
    final String deleteFromFriendship = "DELETE FROM FRIENDSHIP WHERE FIRST_USER_ID = ? AND SECOND_USER_ID = ? " + "OR FIRST_USER_ID = ? AND SECOND_USER_ID = ?";
    final String selectStatusFriendship = "SELECT STATUS FROM FRIENDSHIP WHERE FIRST_USER_ID = ? AND SECOND_USER_ID = ? " + "OR FIRST_USER_ID = ? AND SECOND_USER_ID = ?";

    @Override
    public List<Long> getAllById(long id) {
        return jdbcTemplate.query(selectSecondUser, (rs, rowNum) -> rs.getLong("SECOND_USER_ID"), id, id);
    }

    @Override
    public void add(Friendship friendship) {
        jdbcTemplate.update(insertIntoFriendship, friendship.getUser1ById(), friendship.getUser2ById());
    }

    @Override
    public void put(Friendship friendship) {
        jdbcTemplate.update(updateSetStatus, true, friendship.getUser1ById(), friendship.getUser2ById(), friendship.getUser2ById(), friendship.getUser1ById());
    }

    @Override
    public Optional<Friendship> findFriendship(Friendship friendship) {
        try {
            friendship = jdbcTemplate.queryForObject(selectFromFriendship, FRIENDSHIP_MAPPER, friendship.getUser1ById(), friendship.getUser2ById(), friendship.getUser2ById(), friendship.getUser1ById());
            assert friendship != null;
            return Optional.of(friendship);
        } catch (EmptyResultDataAccessException exception) {
            log.info("Пользователь id{} и пользователь id {} пока не друзья ", friendship.getUser1ById(), friendship.getUser2ById());
            return Optional.empty();
        }
    }

    @Override
    public void delete(Friendship friendship) {
        jdbcTemplate.update(deleteFromFriendship, friendship.getUser1ById(), friendship.getUser2ById(), friendship.getUser2ById(), friendship.getUser1ById());
    }

    @Override
    public boolean status(Friendship friendship) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(selectStatusFriendship, friendship.getUser1ById(), friendship.getUser2ById(), friendship.getUser2ById(), friendship.getUser1ById());
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

