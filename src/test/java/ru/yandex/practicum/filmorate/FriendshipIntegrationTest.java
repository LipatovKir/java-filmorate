package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
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

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final LocalDate testBirthday = LocalDate.of(1982, 10, 9);
    private final User userOne = new User(null, "test@yandex.ru", "Lipatov Kirill", "lipatovKIR", testBirthday);
    private final User userTwo = new User(null, "tests@yandex.ru", "Yandex Kirill", "yandexKIR", testBirthday);
    private final User userThree = new User(null, "tests@mail.ru", "Practicumov Kir", "practKir", testBirthday);

    @AfterEach
    void afterEach() {
        for (User user : userStorage.getAllUsers()) {
            userStorage.deleteUser(user);
        }
    }

    @Test
    void shouldAddAndFindFriends() {
        User userDbOne = userStorage.addUser(userOne);
        User userDbTwo = userStorage.addUser(userTwo);
        User userDbThree = userStorage.addUser(userThree);
        Friendship friendshipTest = new Friendship(userDbOne.getId(), userDbTwo.getId());
        Friendship friendshipTestTwo = new Friendship(userDbOne.getId(), userDbThree.getId());
        friendshipStorage.add(friendshipTest);
        friendshipStorage.add(friendshipTestTwo);
        List<Long> friendsListUserTest = friendshipStorage.getAllById(userDbOne.getId());
        List<Long> friendsListUserTestTwo = friendshipStorage.getAllById(userDbTwo.getId());
        List<Long> friendsListUserTestThree = friendshipStorage.getAllById(userDbThree.getId());
        assertTrue(friendsListUserTest.contains(userDbTwo.getId()));
        assertTrue(friendsListUserTest.contains(userDbThree.getId()));
        assertTrue(friendsListUserTestTwo.isEmpty());
        assertTrue(friendsListUserTestThree.isEmpty());
    }

    @Test
    void shouldPutAndCheckStatusFriends() {
        User userDbOne = userStorage.addUser(userOne);
        User userDbTwo = userStorage.addUser(userTwo);
        User userDbThree = userStorage.addUser(userThree);
        Friendship friendshipTest = new Friendship(userDbOne.getId(), userDbTwo.getId());
        Friendship friendshipTestTwo = new Friendship(userDbTwo.getId(), userDbOne.getId());
        Friendship friendshipTestThree = new Friendship(userDbOne.getId(), userDbThree.getId());
        friendshipStorage.add(friendshipTest);
        friendshipStorage.add(friendshipTestTwo);
        friendshipStorage.add(friendshipTestThree);
        friendshipStorage.put(friendshipTest);
        assertTrue(friendshipStorage.status(friendshipTest));
        assertTrue(friendshipStorage.status(friendshipTestTwo));
        assertFalse(friendshipStorage.status(friendshipTestThree));
    }

    @Test
    void shouldFindFriendshipById() {
        User userDbOne = userStorage.addUser(userOne);
        User userDbTwo = userStorage.addUser(userTwo);
        Friendship friendship = new Friendship(userDbOne.getId(), userDbTwo.getId());
        friendshipStorage.add(friendship);
        Optional<Friendship> optionalFriendship = friendshipStorage.findFriendship(friendship);
        assertThat(optionalFriendship)
                .hasValueSatisfying(friendShip ->
                        assertThat(friendShip)
                                .hasFieldOrPropertyWithValue("userId", userDbOne.getId())
                                .hasFieldOrPropertyWithValue("friendId", userDbTwo.getId()));
    }

    @Test
    void shouldRemoveFriendsList() {
        User userDbOne = userStorage.addUser(userOne);
        User userDbTwo = userStorage.addUser(userTwo);
        Friendship friendship = new Friendship(userDbOne.getId(), userDbTwo.getId());
        friendshipStorage.add(friendship);
        assertThat(friendshipStorage.findFriendship(friendship)).isPresent();
        friendshipStorage.delete(friendship);
        assertThat(friendshipStorage.findFriendship(friendship)).isNotPresent();
    }
}
