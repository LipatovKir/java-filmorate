package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static UserController userController;
    private final String testEmail = "test@yandex.ru";
    private final String testName = "Lipatov Kirill";
    private final String testLogin = "lipatovKIR";
    private final LocalDate testBirthday = LocalDate.of(1982,10,9);

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        UserController.setId(1);
        userController.users.clear();
    }

    @Test
    public void shouldCreateUser() {
        User user = new User(null, testEmail, testName, testLogin, testBirthday);
        userController.createUser(user);
        assertFalse(userController.users.isEmpty());
        assertEquals(1, userController.users.size());
    }

    @Test
    public void shouldNotCreateUserWithNoValidEmail() {
        User user = new User(null, "test_yandex.ru", testName, testLogin, testBirthday);
        assertThrows(
                ValidationException.class,
                () -> {
                    userController.createUser(user);
                    throw new ValidationException("Электронная почта некорректна или не содержит символ @");
                }
        );
        assertTrue(userController.users.isEmpty());
        User user1 = new User(null, null, testName, testLogin, testBirthday);
        assertThrows(
                ValidationException.class,
                () -> {
                    userController.createUser(user1);
                    throw new ValidationException("Электронная почта некорректна или не содержит символ @");
                }
        );
        assertTrue(userController.users.isEmpty());
    }

    @Test
    public void shouldNotCreateUserWithNotValidLogin() {
        User user = new User(null, testEmail, testName,"Stepan stepan", testBirthday);
        assertThrows(
                ValidationException.class,
                () -> {
                    userController.createUser(user);
                    throw new ValidationException("Логин некорректен.");
                }
        );
        assertTrue(userController.users.isEmpty());
        User user1 = new User(null, testEmail, testName, null, testBirthday);
        assertThrows(
                ValidationException.class,
                () -> {
                    userController.createUser(user1);
                    throw new ValidationException("Логин некорректен.");
                }
        );
        assertTrue(userController.users.isEmpty());
    }

    @Test
    public void shouldCreateUserWithEmptyName() {
        User user = new User(null, testEmail, null, testLogin, testBirthday);
        userController.createUser(user);
        assertFalse(userController.users.isEmpty());
        assertEquals(1, userController.users.size());
        assertEquals("{1=User(id=1, email=test@yandex.ru, name=lipatovKIR, login=lipatovKIR, birthday=1982-10-09)}",
                userController.users.toString());
    }

    @Test
    public void shouldNotCreateUserWithFutureBirthday() {
        User user = new User(null, testEmail, testName, testLogin, LocalDate.of(2023,4,15));
        assertThrows(
                ValidationException.class,
                () -> {
                    userController.createUser(user);
                    throw new ValidationException("Дата рождения некорректна.");
                }
        );
        assertTrue(userController.users.isEmpty());
        User user1 = new User(null, testEmail, testName, testLogin, LocalDate.of(2023,4,7));
        userController.createUser(user1);
        assertEquals(1, userController.users.size());
    }

    @Test
    public void shouldNotUpdateUser() {
        assertThrows(
                NullPointerException.class,
                () -> {
                    userController.updateUser(userController.users.get(6));
                    throw new ValidationException("Такого пользователя нет в списке.  ");
                }
        );
    }

    @Test
    public void shouldGetAllUsers() {
        User user1 = new User(null, testEmail, testName, testLogin, testBirthday);
        userController.createUser(user1);
        User user2 = new User(null, testEmail, testName, testLogin, LocalDate.of(1983,1,1));
        userController.createUser(user2);
        User user3 = new User(null, testEmail, testName, testLogin, LocalDate.of(1984,1,1));
        userController.createUser(user3);
        assertEquals(3, userController.getAllUsers().size());
    }
}

