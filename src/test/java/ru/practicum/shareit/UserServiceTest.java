package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.InvalidEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserMapper userMapper;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    @Test
    public void shouldCreateUserStandardCase() {
        UserDto expected = new UserDto(0L, "name", "email@email.com");
        User user = new User();
        user.setId(0L);
        user.setName("name");
        user.setEmail("email@email.com");

        Mockito.when(userMapper.toUser(expected)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toUserDto(user)).thenReturn(expected);

        UserDto actual = userService.create(expected);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        Assertions.assertEquals(0L, savedUser.getId());
        Assertions.assertEquals("name", savedUser.getName());
        Assertions.assertEquals("email@email.com", savedUser.getEmail());
    }

    @Test
    public void shouldCreateUserInvalidEmailCase() {
        UserDto expected = new UserDto(0L, "name", "email@email.com");
        User user = new User();
        user.setId(0L);
        user.setName("name");
        user.setEmail("email@email.com");

        Mockito.when(userMapper.toUser(expected)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenThrow(new RuntimeException());

        InvalidEmailException exp =
            Assertions.assertThrows(InvalidEmailException.class,
                () -> userService.create(expected));

        Assertions.assertEquals("Email: email@email.com уже используется", exp.getMessage());
    }

    @Test
    public void shouldUpdateUserUserNotFoundCase() {
        UserDto userDto = new UserDto();
        User user = new User();
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class,
            () -> userService.update(0L, userDto));
        Mockito.verify(userMapper, Mockito.never()).toUser(userDto);
        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    public void shouldUpdateUserNewUserNotUniqueEmailCase() {
        UserDto userDto = new UserDto(1L, null, "notuniqueemail@email.com");
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("name");
        oldUser.setEmail("email@email.com");

        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("notuniqueemail@email.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        Mockito.when(userMapper.toUser(userDto)).thenReturn(newUser);
        Mockito.when(userRepository.findByEmail(newUser.getEmail())).thenReturn(new User());

        Assertions.assertThrows(InvalidEmailException.class, () -> userService.update(1L, userDto));
    }

    @Test
    public void shouldUpdateUserNewUserNameNullNewUniqueEmailCase() {
        UserDto userDto = new UserDto(1L, null, "new@email.com");
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("name");
        oldUser.setEmail("email@email.com");

        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("new@email.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        Mockito.when(userMapper.toUser(userDto)).thenReturn(newUser);

        userService.update(1L, userDto);

        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        Assertions.assertEquals(1L, savedUser.getId());
        Assertions.assertEquals("name", savedUser.getName());
        Assertions.assertEquals("new@email.com", savedUser.getEmail());
    }

    @Test
    public void shouldUpdateUserNewUserEmailNullNewNameCase() {
        UserDto userDto = new UserDto(1L, "new name", null);
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("name");
        oldUser.setEmail("email@email.com");

        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("new name");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        Mockito.when(userMapper.toUser(userDto)).thenReturn(newUser);

        userService.update(1L, userDto);

        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        Assertions.assertEquals(1L, savedUser.getId());
        Assertions.assertEquals("new name", savedUser.getName());
        Assertions.assertEquals("email@email.com", savedUser.getEmail());
    }

    @Test
    public void shouldReadStandardCase() {
        Long id = 2L;
        User user = new User();
        UserDto expected = new UserDto();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserDto(user)).thenReturn(expected);

        UserDto actual = userService.read(id);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldReadUserNotFoundCase() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class,
            () -> userService.read(0L));
    }

    @Test
    public void shouldDeleteUserNotFoundCase() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class,
            () -> userService.delete(0L));
    }

    @Test
    public void shouldReadAllStandardCase() {
        List<UserDto> expected = List.of(new UserDto());
        Mockito.when(userMapper.listToUserDto(Mockito.anyList())).thenReturn(expected);

        List<UserDto> actual = userService.readAll();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldUpdateUserNewUserEmailEqualsCase() {
        UserDto userDto = new UserDto(1L, "new name", "email@email.com");
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("name");
        oldUser.setEmail("email@email.com");

        User newUser = new User();
        newUser.setId(1L);
        newUser.setName("new name");
        newUser.setEmail("email@email.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        Mockito.when(userMapper.toUser(userDto)).thenReturn(newUser);

        userService.update(1L, userDto);

        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        Assertions.assertEquals(1L, savedUser.getId());
        Assertions.assertEquals("new name", savedUser.getName());
        Assertions.assertEquals("email@email.com", savedUser.getEmail());
    }
}
