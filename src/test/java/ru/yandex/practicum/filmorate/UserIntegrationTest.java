package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    final UserStorage userStorage;
    final String testEmail = "test@yandex.ru";
    final String testName = "Lipatov Kirill";
    final String testLogin = "lipatovKIR";
    final LocalDate testBirthday = LocalDate.of(1982, 10, 9);
    final String testOneName = "Liftoff Kirill";
    final String testOneEmail = "tests@yandex.ru";
    final String testOneLogin = "lipatoffKIR";
    static User userOne;
    static User userTwo;

    @BeforeEach
    void beforeEach() {
        userOne = new User(null, testEmail, testName, testLogin, testBirthday);
        userTwo = new User(null, testOneEmail, testOneName, testOneLogin, testBirthday);
    }

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
        try {
            userStorage.addUser(userOne);
            assertThrows(DataIntegrityViolationException.class, () -> {
            });
        } catch (DataIntegrityViolationException e) {
            System.out.println("Электронная почта некорректна или не содержит символ @");
        }
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
        try {
            userStorage.addUser(userNew);
            assertThrows(DataIntegrityViolationException.class, () -> {
            });
        } catch (DataIntegrityViolationException e) {
            System.out.println("Логин некорректен.");
        }
    }

    @Test
    void shouldNotCreateUserWithFutureBirthday() {
        User user = new User(null, testEmail, testName, testLogin, LocalDate.of(2023, 6, 30));
        try {
            userStorage.addUser(user);
            assertThrows(DataIntegrityViolationException.class, () -> {
            });
        } catch (DataIntegrityViolationException e) {
            System.out.println("Дата рождения некорректна.");
        }
        assertTrue(userStorage.getAllUsers().isEmpty());
        User userNew = new User(null, testEmail, testName, testLogin, LocalDate.of(2023, 4, 7));
        userStorage.addUser(userNew);
        assertEquals(1, userStorage.getAllUsers().size());
    }

    @Test
    void shouldFindUserById() {
        final long id = userStorage.addUser(userOne).getId();
        final Optional<User> userOptional = userStorage.findUserById(id);
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
        final User userNew = userStorage.addUser(userOne);
        final User userLast = userStorage.addUser(userTwo);
        final List<User> allUsers = userStorage.getAllUsers();
        assertThat(allUsers).isNotNull().hasSize(2);
        assertTrue(allUsers.contains(userNew));
        assertTrue(allUsers.contains(userLast));
    }

    @Test
    void shouldDeleteUser() {
        final User user = userStorage.addUser(userOne);
        final long id = user.getId();
        userStorage.deleteUser(user);
        final Optional<User> userOptional = userStorage.findUserById(id);
        assertFalse(userOptional.isPresent());
    }
}