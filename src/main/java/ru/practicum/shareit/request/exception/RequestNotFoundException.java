package ru.practicum.shareit.request.exception;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long requestId) {
        super("Запрос вещи с requestId = " + requestId + " не найден!");
    }
}
