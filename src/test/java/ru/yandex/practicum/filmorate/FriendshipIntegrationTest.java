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
class FriendshipIntegrationTest {

    final UserStorage userStorage;
    final FriendshipStorage friendshipStorage;

    static User userOne;
    static User userTwo;
    static User userThree;
    static User userDbOne;
    static User userDbTwo;
    static User userDbThree;
    static LocalDate testBirthday = LocalDate.of(1982, 10, 9);

    @BeforeEach
    void beforeEach() {
        userOne = new User(null, "test@yandex.ru", "Lipatov Kirill", "lipatovKIR", testBirthday);
        userTwo = new User(null, "tests@yandex.ru", "Yandex Kirill", "yandexKIR", testBirthday);
        userThree = new User(null, "tests@mail.ru", "Practicumov Kir", "practKir", testBirthday);
    }

    @AfterEach
    void afterEach() {
        for (User user : userStorage.getAllUsers()) {
            userStorage.deleteUser(user);
        }
    }

    @Test
    void shouldAddAndFindFriends() {
        userDbOne = userStorage.addUser(userOne);
        userDbTwo = userStorage.addUser(userTwo);
        userDbThree = userStorage.addUser(userThree);
        final Friendship friendship12 = new Friendship(userDbOne.getId(), userDbTwo.getId());
        final Friendship friendship13 = new Friendship(userDbOne.getId(), userDbThree.getId());
        friendshipStorage.add(friendship12);
        friendshipStorage.add(friendship13);
        final List<Long> friendsListUser1 = friendshipStorage.getAllById(userDbOne.getId());
        final List<Long> friendsListUser2 = friendshipStorage.getAllById(userDbTwo.getId());
        final List<Long> friendsListUser3 = friendshipStorage.getAllById(userDbThree.getId());
        assertTrue(friendsListUser1.contains(userDbTwo.getId()));
        assertTrue(friendsListUser1.contains(userDbThree.getId()));
        assertTrue(friendsListUser2.isEmpty());
        assertTrue(friendsListUser3.isEmpty());
    }

    @Test
    void shouldPutAndCheckStatusFriends() {
        userDbOne = userStorage.addUser(userOne);
        userDbTwo = userStorage.addUser(userTwo);
        userDbThree = userStorage.addUser(userThree);
        final Friendship friendship12 = new Friendship(userDbOne.getId(), userDbTwo.getId());
        final Friendship friendship21 = new Friendship(userDbTwo.getId(), userDbOne.getId());
        final Friendship friendship13 = new Friendship(userDbOne.getId(), userDbThree.getId());
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
        userDbOne = userStorage.addUser(userOne);
        userDbTwo = userStorage.addUser(userTwo);
        final Friendship friendship = new Friendship(userDbOne.getId(), userDbTwo.getId());
        friendshipStorage.add(friendship);
        final Optional<Friendship> optionalFriendship = friendshipStorage.findFriendship(friendship);
        assertThat(optionalFriendship)
                .hasValueSatisfying(friendShip ->
                        assertThat(friendShip)
                                .hasFieldOrPropertyWithValue("user1ById", userDbOne.getId())
                                .hasFieldOrPropertyWithValue("user2ById", userDbTwo.getId()));
    }

    @Test
    void shouldRemoveFriendsList() {
        userDbOne = userStorage.addUser(userOne);
        userDbTwo = userStorage.addUser(userTwo);
        final Friendship friendship = new Friendship(userDbOne.getId(), userDbTwo.getId());
        friendshipStorage.add(friendship);
        assertThat(friendshipStorage.findFriendship(friendship)).isPresent();
        friendshipStorage.delete(friendship);
        assertThat(friendshipStorage.findFriendship(friendship)).isNotPresent();
    }
}
