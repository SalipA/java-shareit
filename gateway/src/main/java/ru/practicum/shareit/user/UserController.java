package ru.practicum.shareit.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Пользователи", description = "Операции для работы с пользователями")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Operation(
        summary = "Регистрация пользователя",
        description = "Позволяет зарегистрировать пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
                @Content(
                    mediaType = "application/json", schema = @Schema(implementation = UserDto.class)
                    )
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = {
                @Content(
                    mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Email должен иметь формат адреса электронной почты\"}")
                )
            })
    })
    public ResponseEntity<Object> create(@Valid @RequestBody @Parameter(required = true,
        description = "Сущность пользователя") UserDto userDto) {
        log.info("POST: /users, value = {}", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    @Operation(
        summary = "Изменение данных пользователя",
        description = "Позволяет изменить имя пользователя и/или его email"
    )
    public ResponseEntity<Object> update(@PathVariable @Parameter(description = "id пользователя", example = "1") Long userId,
                                         @RequestBody @Parameter(required = true,
                                             description = "Сущность пользователя") UserDto userDto) {
        log.info("PATCH: /users/{}, value = {}", userId, userDto);
        return userClient.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    @Operation(
        summary = "Получение данных пользователя",
        description = "Позволяет получить данные о пользователе по его id"
    )
    public ResponseEntity<Object> read(@PathVariable Long userId) {
        log.info("GET: /users/{}", userId);
        return userClient.read(userId);
    }

    @DeleteMapping("/{userId}")
    @Operation(
        summary = "Удаление пользователя",
        description = "Позволяет удалить пользователя"
    )
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("DELETE: /users/{}", userId);
        return userClient.delete(userId);
    }

    @GetMapping
    @Operation(
        summary = "Получение данных о всех пользователях сервиса",
        description = "Позволяет получить список пользователей сервиса"
    )
    public ResponseEntity<Object> readAll() {
        log.info("GET: /users ");
        return userClient.readAll();
    }
}
