package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.InvalidEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> idUsers = new HashMap<>();
    private final Map<Long, String> idEmails = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public User create(User user) {
        idCounter++;
        user.setId(idCounter);
        idUsers.put(idCounter, user);
        idEmails.put(idCounter, user.getEmail());
        log.info("User id = {} has been created", user.getId());
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        idUsers.remove(userId);
        idEmails.remove(userId);
        idUsers.put(userId, user);
        idEmails.put(userId, user.getEmail());
        log.info("User id = {} has been updated", userId);
        return user;
    }

    @Override
    public User read(Long userId) {
        log.info(idUsers.get(userId).toString());
        return idUsers.get(userId);
    }

    @Override
    public void delete(Long userId) {
        idUsers.remove(userId);
        idEmails.remove(userId);
        log.info("User id = {} has been deleted", userId);
    }

    @Override
    public List<User> readAll() {
        log.info(idUsers.values().toString());
        return new ArrayList<>(idUsers.values());
    }

    @Override
    public void checkUserId(Long userId) {
        if (!idUsers.containsKey(userId)) {
            log.error("User id = {} is not found", userId);
            throw new UserNotFoundException(userId);
        }
    }

    @Override
    public void checkEmailDistinct(User user) {
        if (idEmails.containsValue(user.getEmail())) {
            log.error("Users email = {} is not distinct", user.getEmail());
            throw new InvalidEmailException(user.getEmail());
        }
    }
}