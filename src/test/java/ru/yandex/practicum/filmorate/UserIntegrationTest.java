package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class UserIntegrationTest {

    private final UserStorage userStorage;
    private final String testEmail = "test@yandex.ru";
    private final String testName = "Lipatov Kirill";
    private final String testLogin = "lipatovKIR";
    private final LocalDate testBirthday = LocalDate.of(1982, 10, 9);
    private final String testOneName = "Liftoff Kirill";
    private final String testOneEmail = "tests@yandex.ru";
    private final String testOneLogin = "lipatoffKIR";
    private final User userOne = new User(null, testEmail, testName, testLogin, testBirthday);
    private final User userTwo = new User(null, testOneEmail, testOneName, testOneLogin, testBirthday);

    @AfterEach
    void afterEach() {
        for (User user : userStorage.getAllUsers()) {
            userStorage.deleteUser(user);
        }
    }

    @Test
    void shouldCreateUser() {
        final User userNew = userStorage.addUser(userOne);
        assertThat(userNew.getId()).isNotNull();
        assertThat(userNew.getEmail()).isEqualTo(userOne.getEmail());
        assertThat(userNew.getName()).isEqualTo(userOne.getName());
        assertThat(userNew.getLogin()).isEqualTo(userOne.getLogin());
        assertThat(userNew.getBirthday()).isEqualTo(userOne.getBirthday());
    }

    @Test
    void shouldNotCreateUserWithNoValidEmail() {
        User userOne = new User(null, null, testName, testLogin, testBirthday);
        assertThrows(DataIntegrityViolationException.class, () -> userStorage.addUser(userOne));
    }

    @Test
    void shouldPutUser() {
        final User dbUserOne = userStorage.addUser(userOne);
        final long id = dbUserOne.getId();
        User testUserTwo = new User(id,
                userTwo.getEmail(),
                userTwo.getName(),
                userTwo.getLogin(),
                userTwo.getBirthday());
        userStorage.putUser(testUserTwo);
        final Optional<User> userOptional = userStorage.findUserById(id);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", id)
                                .hasFieldOrPropertyWithValue("email", userTwo.getEmail())
                                .hasFieldOrPropertyWithValue("name", userTwo.getName())
                                .hasFieldOrPropertyWithValue("login", userTwo.getLogin())
                                .hasFieldOrPropertyWithValue("birthday", userTwo.getBirthday())
                );
    }

    @Test
    void shouldNotCreateUserWithNotValidLogin() {
        User userNew = new User(null, testEmail, testName, null, testBirthday);
        assertThrows(DataIntegrityViolationException.class, () -> userStorage.addUser(userNew));
    }

    @Test
    void shouldNotCreateUserWithFutureBirthday() {
        User user = new User(null, testEmail, testName, testLogin, LocalDate.of(2023, 6, 30));
        assertThrows(DataIntegrityViolationException.class, () -> userStorage.addUser(user));
        assertTrue(userStorage.getAllUsers().isEmpty());
        User userNew = new User(null, testEmail, testName, testLogin, LocalDate.of(2023, 4, 7));
        userStorage.addUser(userNew);
        assertEquals(1, userStorage.getAllUsers().size());
    }

    @Test
    void shouldFindUserById() {
        long id = userStorage.addUser(userOne).getId();
        Optional<User> userOptional = userStorage.findUserById(id);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", id)
                                .hasFieldOrPropertyWithValue("email", userOne.getEmail())
                                .hasFieldOrPropertyWithValue("name", userOne.getName())
                                .hasFieldOrPropertyWithValue("login", userOne.getLogin())
                                .hasFieldOrPropertyWithValue("birthday", userOne.getBirthday())
                );
    }

    @Test
    void shouldFindAllUsers() {
        User userNew = userStorage.addUser(userOne);
        User userLast = userStorage.addUser(userTwo);
        List<User> allUsers = userStorage.getAllUsers();
        assertThat(allUsers).isNotNull().hasSize(2);
        assertTrue(allUsers.contains(userNew));
        assertTrue(allUsers.contains(userLast));
    }

    @Test
    void shouldDeleteUser() {
        User user = userStorage.addUser(userOne);
        long id = user.getId();
        userStorage.deleteUser(user);
        Optional<User> userOptional = userStorage.findUserById(id);
        assertFalse(userOptional.isPresent());
    }
}