package ru.practicum.shareit;

public class PaginationParamException extends RuntimeException {
    public PaginationParamException(Integer from, Integer size) {
        super("Не возможно обработать запрос с переданными параметрами пагинации: from = " + from + ", size = " + size);
    }
}
