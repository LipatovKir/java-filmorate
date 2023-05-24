package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
    final String test1Name = "Lipatoff Kirill";
    final String test1Email = "tests@yandex.ru";
    final String test1Login = "lipatoffKIR";
    static User user1;
    static User user2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(null, testEmail, testName, testLogin, testBirthday);
        user2 = new User(null, test1Email, test1Name, test1Login, testBirthday);
    }

    @AfterEach
    void afterEach() {
        for (User user : userStorage.getAllUsers()) {
            userStorage.deleteUser(user);
        }
    }

    @Test
    public void shouldCreateUser() {
        final User user11 = userStorage.addUser(user1);
        assertThat(user11.getId()).isNotNull();
        assertThat(user11.getEmail()).isEqualTo(user1.getEmail());
        assertThat(user11.getName()).isEqualTo(user1.getName());
        assertThat(user11.getLogin()).isEqualTo(user1.getLogin());
        assertThat(user11.getBirthday()).isEqualTo(user1.getBirthday());
    }

    @Test
    public void shouldNotCreateUserWithNoValidEmail() {
        User user = new User(null, "test_yandex.ru", testName, testLogin, testBirthday);
        assertThrows(ValidationException.class, () -> {
            userStorage.addUser(user);
            throw new ValidationException("Электронная почта некорректна или не содержит символ @");
        });
        User user1 = new User(null, null, testName, testLogin, testBirthday);
        assertThrows(DataIntegrityViolationException.class, () -> {
            userStorage.addUser(user1);
            throw new DataIntegrityViolationException("Электронная почта некорректна или не содержит символ @");
        });
    }

    @Test
    void shouldPutUser() {
        final User dbUser1 = userStorage.addUser(user1);
        final long id = dbUser1.getId();
        User testUser2 = new User(id,
                user2.getEmail(),
                user2.getName(),
                user2.getLogin(),
                user2.getBirthday());
        userStorage.putUser(testUser2);
        final Optional<User> userOptional = userStorage.findUserById(id);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", id)
                                .hasFieldOrPropertyWithValue("email", user2.getEmail())
                                .hasFieldOrPropertyWithValue("name", user2.getName())
                                .hasFieldOrPropertyWithValue("login", user2.getLogin())
                                .hasFieldOrPropertyWithValue("birthday", user2.getBirthday())
                );
    }

    @Test
    public void shouldNotCreateUserWithNotValidLogin() {
        User user = new User(null, testEmail, testName, "Stepan stepan", testBirthday);
        assertThrows(ValidationException.class, () -> {
            userStorage.addUser(user);
            throw new ValidationException("Логин некорректен.");
        });

        User user1 = new User(null, testEmail, testName, null, testBirthday);
        assertThrows(DataIntegrityViolationException.class, () -> {
            userStorage.addUser(user1);
            throw new DataIntegrityViolationException("Логин некорректен.");
        });
    }

    @Test
    public void shouldNotCreateUserWithFutureBirthday() {
        User user = new User(null, testEmail, testName, testLogin, LocalDate.of(2023, 6, 30));
        assertThrows(DataIntegrityViolationException.class, () -> {
            userStorage.addUser(user);
            throw new DataIntegrityViolationException("Дата рождения некорректна.");
        });
        assertTrue(userStorage.getAllUsers().isEmpty());
        User user1 = new User(null, testEmail, testName, testLogin, LocalDate.of(2023, 4, 7));
        userStorage.addUser(user1);
        assertEquals(1, userStorage.getAllUsers().size());
    }

    @Test
    void shouldFindUserById() {
        final long id = userStorage.addUser(user1).getId();
        final Optional<User> userOptional = userStorage.findUserById(id);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", id)
                                .hasFieldOrPropertyWithValue("email", user1.getEmail())
                                .hasFieldOrPropertyWithValue("name", user1.getName())
                                .hasFieldOrPropertyWithValue("login", user1.getLogin())
                                .hasFieldOrPropertyWithValue("birthday", user1.getBirthday())
                );
    }

    @Test
    void shouldFindAllUsers() {
        final User user11 = userStorage.addUser(user1);
        final User user22 = userStorage.addUser(user2);
        final List<User> allUsers = userStorage.getAllUsers();
        assertThat(allUsers).isNotNull();
        assertThat(allUsers.size()).isEqualTo(2);
        assertTrue(allUsers.contains(user11));
        assertTrue(allUsers.contains(user22));
    }

    @Test
    void shouldDeleteUser() {
        final User user = userStorage.addUser(user1);
        final long id = user.getId();
        userStorage.deleteUser(user);
        final Optional<User> userOptional = userStorage.findUserById(id);
        assertFalse(userOptional.isPresent());
    }
}