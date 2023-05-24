package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class FriendshipIntegrationTest {

    final UserStorage userStorage;
    final FriendshipStorage friendshipStorage;

    static User user1;
    static User user2;
    static User user3;
    static User userDB1;
    static User userDB2;
    static User userDB3;
    static LocalDate testBirthday = LocalDate.of(1982, 10, 9);

    @BeforeEach
    void beforeEach() {
        user1 = new User(null, "test@yandex.ru", "Lipatov Kirill", "lipatovKIR", testBirthday);
        user2 = new User(null, "tests@yandex.ru", "Yandex Kirill", "yandexKIR", testBirthday);
        user3 = new User(null, "tests@mail.ru", "Practicumov Kir", "practKir", testBirthday);
    }

    @AfterEach
    void afterEach() {
        for (User user : userStorage.getAllUsers()) {
            userStorage.deleteUser(user);
        }
    }

    @Test
    void shouldAddAndFindFriends() {
        userDB1 = userStorage.addUser(user1);
        userDB2 = userStorage.addUser(user2);
        userDB3 = userStorage.addUser(user3);
        final Friendship friendship12 = new Friendship(userDB1.getId(), userDB2.getId());
        final Friendship friendship13 = new Friendship(userDB1.getId(), userDB3.getId());
        friendshipStorage.add(friendship12);
        friendshipStorage.add(friendship13);
        final List<Long> friendsListUser1 = friendshipStorage.getAllById(userDB1.getId());
        final List<Long> friendsListUser2 = friendshipStorage.getAllById(userDB2.getId());
        final List<Long> friendsListUser3 = friendshipStorage.getAllById(userDB3.getId());
        assertTrue(friendsListUser1.contains(userDB2.getId()));
        assertTrue(friendsListUser1.contains(userDB3.getId()));
        assertTrue(friendsListUser2.isEmpty());
        assertTrue(friendsListUser3.isEmpty());
    }

    @Test
    void shouldPutAndCheckStatusFriends() {
        userDB1 = userStorage.addUser(user1);
        userDB2 = userStorage.addUser(user2);
        userDB3 = userStorage.addUser(user3);
        final Friendship friendship12 = new Friendship(userDB1.getId(), userDB2.getId());
        final Friendship friendship21 = new Friendship(userDB2.getId(), userDB1.getId());
        final Friendship friendship13 = new Friendship(userDB1.getId(), userDB3.getId());
        friendshipStorage.add(friendship12);
        friendshipStorage.add(friendship21);
        friendshipStorage.add(friendship13);
        friendshipStorage.put(friendship12);
        assertTrue(friendshipStorage.status(friendship12));
        assertTrue(friendshipStorage.status(friendship21));
        assertFalse(friendshipStorage.status(friendship13));
    }

    @Test
    void shouldFindFriendshipById() {
        userDB1 = userStorage.addUser(user1);
        userDB2 = userStorage.addUser(user2);
        final Friendship friendship = new Friendship(userDB1.getId(), userDB2.getId());
        friendshipStorage.add(friendship);
        final Optional<Friendship> optionalFriendship = friendshipStorage.findFriendship(friendship);
        assertThat(optionalFriendship)
                .hasValueSatisfying(friendShip ->
                        assertThat(friendShip)
                                .hasFieldOrPropertyWithValue("user1ById", userDB1.getId())
                                .hasFieldOrPropertyWithValue("user2ById", userDB2.getId()));
    }

    @Test
    void shouldRemoveFriendsList() {
        userDB1 = userStorage.addUser(user1);
        userDB2 = userStorage.addUser(user2);
        final Friendship friendship = new Friendship(userDB1.getId(), userDB2.getId());
        friendshipStorage.add(friendship);
        assertThat(friendshipStorage.findFriendship(friendship)).isPresent();
        friendshipStorage.delete(friendship);
        assertThat(friendshipStorage.findFriendship(friendship)).isNotPresent();
    }
}
