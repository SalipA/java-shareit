package ru.practicum.shareit.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Бронирования", description = "Операции для работы с заявками на бронь вещи")
public class BookingController {
    private final BookingClient bookingClient;

    @Operation(summary = "Создание заявки на бронирование вещи",
        description = "Позволяет пользователю создать заявку на бронирование вещи")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", schema =
                @Schema(implementation = BookingDto.class))}),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {
            @Content(mediaType = "application/json", examples =
                @ExampleObject(value = "{\"error\": \"Дата начала аренды должна быть текущей или в будущем\"}"))})})
    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(name = "X-Sharer-User-Id")
            @Parameter(description = "id " + "аутентифицированного пользователя сервиса", example = "1")
                Long userId,
            @Valid
            @RequestBody
            @BookingStartEndTimeConstraint
                BookingDto bookingDto) {
        log.info("POST: /bookings, userId = {}, value = {}", userId, bookingDto);
        return bookingClient.create(userId, bookingDto);
    }

    @Operation(summary = "Получение информации о бронировании",
        description = "Позволяет пользователю, создавшему заявку на бронирование, и владельцу вещи получить информацию " +
            "о бронировании ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", schema =
                @Schema(implementation = BookingDto.class))}),
        @ApiResponse(responseCode = "400", description = "Bad request", content = {
            @Content(mediaType = "application/json", examples =
                @ExampleObject(value = "{\"error\": \"У пользователя userId = 1 нет прав для просмотра информации о " +
                    "бронировании bookingId = 2\"}"))}),
        @ApiResponse(responseCode = "404", description = "Not found", content = {
            @Content(mediaType = "application/json", examples =
                @ExampleObject(value = "{\"error\": \"Бронирование с bookingId = 1 не найдено!\"}"))})})
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> read(
            @RequestHeader(name = "X-Sharer-User-Id")
            @Parameter(description = "id аутентифицированного пользователя сервиса", example = "1")
                Long userId,
            @PathVariable
            @Parameter(description = "id бронирования", example = "2")
                Long bookingId) {
        log.info("GET: /bookings/{}, userId = {}", bookingId, userId);
        return bookingClient.read(userId, bookingId);
    }

    @Operation(summary = "Изменения статуса бронирования владельцем вещи",
        description = "Позволяет владельцу одобрять или отклонять пользовательские запросы на бронирование вещей")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", schema =
                @Schema(implementation = BookingDto.class))}),
        @ApiResponse(responseCode = "400", description = "Bad request", content = {
            @Content(mediaType = "application/json", examples =
                @ExampleObject(value = "{\"error\": \"Статус бронирования bookingId = 2 уже был изменен\"}"))}),
        @ApiResponse(responseCode = "404", description = "Not found", content = {@Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": " + "\"Бронирование с bookingId = 2 не найдено!\"}"))})})
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader(name = "X-Sharer-User-Id")
            @Parameter(description = "id аутентифицированного пользователя сервиса, владельца вещи", example = "1")
                Long userId,
            @PathVariable
            @Parameter(description = "id бронирования", example = "2")
                Long bookingId,
            @RequestParam
            @NotNull
            @Parameter(description = "true - подтверждается владельцем, false - отклоняется владельцем", example =
                "false")
                Boolean approved) {
        log.info("PATCH: /bookings/{}, userId = {}", bookingId, userId);
        return bookingClient.approveBooking(userId, bookingId, Boolean.toString(approved));
    }

    @Operation(summary = "Получение списка бронирований пользователем",
        description = "Позволяет пользователю получить список своих бронирований")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", array =
                @ArraySchema(schema = @Schema(implementation = BookingDto.class)))}),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {
            @Content(mediaType = "application/json", examples =
                @ExampleObject(value = "{\"error\": \"Unknown " + "state: NEW\"}"))})})
    @GetMapping
    public ResponseEntity<Object> getAllByState(
            @RequestHeader(name = "X-Sharer-User-Id")
            @Parameter(description = "id аутентифицированного пользователя сервиса", example = "1")
                Long userId,
            @RequestParam(required = false)
            @Parameter(description = "статус бронирования", example = "FUTURE")
                States state,
            @RequestParam(required = false)
            @Nullable
            @Min(0)
            @Parameter(description = "номер страницы, с которой начинается вывод результатов", example = "2")
                Integer from,
            @RequestParam(required = false)
            @Nullable
            @Min(1)
            @Parameter(description = "количество элементов на" + "странице", example = "3")
                Integer size) {
        if (state == null) {
            state = States.ALL;
        }
        log.info("GET: /bookings, userId = {}, state = {}, pagination: from {} size {}", userId, state, from,
            size);
        return bookingClient.getAllByState(userId, state.toString(), from, size);
    }

    @Operation(summary = "Получение списка бронирований владельцем вещи",
        description = "Позволяет владельцу вещи получить список ее бронирований")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = "application/json", array =
                @ArraySchema(schema = @Schema(implementation = BookingDto.class)))}),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = {
            @Content(mediaType = "application/json", examples =
                @ExampleObject(value = "{\"error\": \"Не возможно обработать запрос с переданными параметрами пагинации:" +
                    " from =" + " 0, size = null\"}"))})})
    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerAndState(
            @RequestHeader(name = "X-Sharer-User-Id")
            @Parameter(description = "id аутентифицированного пользователя " + "сервиса, владельца вещи", example = "1")
                Long userId,
            @RequestParam(required = false)
            @Parameter(description = "статус бронирования", example = "FUTURE")
                States state,
            @RequestParam(required = false)
            @Nullable
            @Min(0)
            @Parameter(description = "номер страницы, с которой начинается вывод" + " результатов", example = "2")
                Integer from,
            @RequestParam(required = false)
            @Nullable
            @Min(1)
            @Parameter(description = "количество элементов на странице", example = "3")
                Integer size) {
        if (state == null) {
            state = States.ALL;
        }
        log.info("GET: /bookings/owner, userId = {}, state = {}, pagination: from {} size {}", userId, state, from,
            size);
        return bookingClient.getAllByOwnerAndState(userId, state.toString(), from, size);
    }
}