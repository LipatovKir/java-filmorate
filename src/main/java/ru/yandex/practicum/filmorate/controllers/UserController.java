package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping
    public User delUser(@RequestBody User user) {
        return userService.deleteUser(user);
    }

    @GetMapping("/{userId}")
    public User findUserById(@PathVariable("userId") String userId) {
        return userService.findUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") String id,
                          @PathVariable("friendId") String friendId) {
        return userService.addFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User delFriend(@PathVariable("id") String id,
                          @PathVariable("friendId") String friendId) {
        return userService.delFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> allUserFriends(@PathVariable("id") String id) {
        return userService.friendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> mutualFriends(@PathVariable("id") String id,
                                    @PathVariable("otherId") String otherId) {
        return userService.mutualFriends(id, otherId);
    }
}