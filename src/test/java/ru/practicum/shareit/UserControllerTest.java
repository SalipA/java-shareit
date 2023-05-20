package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @Autowired
    private ShareItAppExceptionController exceptionController;

    @BeforeEach
    public void setup() {
        mockMvc =
            MockMvcBuilders.standaloneSetup(new UserController(userService)).setControllerAdvice(exceptionController)
                .build();
    }

    @SneakyThrows
    @Test
    public void shouldCreateStandardCase() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("email@email.com");

        Mockito.when(userService.create(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("name"))
            .andExpect(jsonPath("$.email").value("email@email.com"));

        Mockito.verify(userService, Mockito.times(1)).create(userDto);
    }

    @SneakyThrows
    @Test
    public void shouldCreateUserNotValidRequestEmailNullCase() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Email не может быть пустым значением"));
        Mockito.verify(userService, Mockito.never()).create(userDto);
    }

    @SneakyThrows
    @Test
    public void shouldCreateUserNotValidRequestEmailInvalidFormatCase() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("invalid email");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Email должен иметь формат адреса электронной " +
                "почты"));
        Mockito.verify(userService, Mockito.never()).create(userDto);
    }

    @SneakyThrows
    @Test
    public void shouldCreateUserNotValidRequestNameNullCase() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("email@email.com");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Имя не может быть пустым значением"));
        Mockito.verify(userService, Mockito.never()).create(userDto);
    }

    @SneakyThrows
    @Test
    public void shouldUpdateUserStandardCase() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("email@email.com");

        Mockito.when(userService.update(1L, userDto)).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("name"))
            .andExpect(jsonPath("$.email").value("email@email.com"));

        Mockito.verify(userService, Mockito.times(1)).update(1L, userDto);
    }

    @SneakyThrows
    @Test
    public void shouldReadUserStandardCase() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("email@email.com");

        Mockito.when(userService.read(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("name"))
            .andExpect(jsonPath("$.email").value("email@email.com"));

        Mockito.verify(userService, Mockito.times(1)).read(1L);
    }

    @SneakyThrows
    @Test
    public void shouldReadUserNotFoundCase() {

        Mockito.when(userService.read(1L)).thenThrow(new UserNotFoundException(1L));

        mockMvc.perform(get("/users/{userId}", 1L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Пользователь с userId = 1 не найден!"));

        Mockito.verify(userService, Mockito.times(1)).read(1L);
    }

    @SneakyThrows
    @Test
    public void shouldDeleteUserNotFoundCase() {

        Mockito.doThrow(new UserNotFoundException(1L)).when(userService).delete(1L);

        mockMvc.perform(delete("/users/{userId}", 1L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Пользователь с userId = 1 не найден!"));

        Mockito.verify(userService, Mockito.times(1)).delete(1L);
    }

    @SneakyThrows
    @Test
    public void shouldUReadAllUserStandardCase() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("email@email.com");

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setName("name2");
        userDto2.setEmail("email2@email.com");

        List<UserDto> userDtoList = List.of(userDto, userDto2);

        Mockito.when(userService.readAll()).thenReturn(userDtoList);

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].name").value("name"))
            .andExpect(jsonPath("$[0].email").value("email@email.com"))
            .andExpect(jsonPath("$[1].id").value("2"))
            .andExpect(jsonPath("$[1].name").value("name2"))
            .andExpect(jsonPath("$[1].email").value("email2@email.com"));

        Mockito.verify(userService, Mockito.times(1)).readAll();
    }
}