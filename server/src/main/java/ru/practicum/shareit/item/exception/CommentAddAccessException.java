package ru.practicum.shareit.item.exception;

public class CommentAddAccessException extends RuntimeException {
    public CommentAddAccessException(Long userId, Long itemId) {
        super("Пользователь с userId =" + userId + " не может добавить комментарий к вещи itemId" + itemId);
    }
}
