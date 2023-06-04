package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.InvalidEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        try {
            User userFromDataBase = userRepository.save(user);
            log.info("User id = {} has been created", userFromDataBase.getId());
            return userMapper.toUserDto(userFromDataBase);
        } catch (RuntimeException exp) {
            log.error("Users email = {} is not distinct", user.getEmail());
            throw new InvalidEmailException(user.getEmail());
        }
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User userFromDataBase = checkUser(userId);
        User user = userMapper.toUser(userDto);
        if (!userFromDataBase.getEmail().equals(user.getEmail())) {
            if (!(userRepository.findByEmail(userDto.getEmail()) == null)) {
                throw new InvalidEmailException(userDto.getEmail());
            }
        }
        if (user.getName() == null) {
            user.setName(userFromDataBase.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userFromDataBase.getEmail());
        }
        user.setId(userId);
        log.info("User id = {} has been updated", userId);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto read(Long userId) {
        return userMapper.toUserDto(checkUser(userId));
    }

    @Override
    public void delete(Long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
        log.info("User id = {} has been deleted", userId);
    }

    @Override
    public List<UserDto> readAll() {
        return userMapper.listToUserDto(userRepository.findAll());
    }

    @Override
    public User checkUser(Long userId) {
        Optional<User> userFromDataBase = userRepository.findById(userId);
        if (userFromDataBase.isPresent()) {
            return userFromDataBase.get();
        } else {
            log.error("User id = {} is not found", userId);
            throw new UserNotFoundException(userId);
        }
    }
}