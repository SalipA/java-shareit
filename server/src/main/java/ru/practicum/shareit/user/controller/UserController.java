package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    public final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("POST: /users, value = {}", userDto);
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("PATCH: /users/{}, value = {}", userId, userDto);
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto read(@PathVariable Long userId) {
        log.info("GET: /users/{}", userId);
        return userService.read(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("DELETE: /users/{}", userId);
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> readAll() {
        log.info("GET: /users ");
        return userService.readAll();
    }
}