package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

    public UserServiceImpl(UserDao userDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        userDao.checkEmailDistinct(user);
        return userMapper.toUserDto(userDao.create(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        userDao.checkUserId(userId);
        User userFromStorage = userDao.read(userId);
        if (!userFromStorage.getEmail().equals(user.getEmail())) {
            userDao.checkEmailDistinct(user);
        }
        if (user.getName() == null) {
            user.setName(userFromStorage.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userFromStorage.getEmail());
        }
        user.setId(userId);
        return userMapper.toUserDto(userDao.update(userId, user));
    }

    @Override
    public UserDto read(Long userId) {
        userDao.checkUserId(userId);
        return userMapper.toUserDto(userDao.read(userId));
    }

    @Override
    public void delete(Long userId) {
        userDao.checkUserId(userId);
        userDao.delete(userId);
    }

    @Override
    public List<UserDto> readAll() {
        return userMapper.listToUserDto(userDao.readAll());
    }

}