package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.PaginationParamException;
import ru.practicum.shareit.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> read(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> readAllByUserId(Long userId, Integer from, Integer size) {
        if (from == null && size != null || size == null && from != null) {
            throw new PaginationParamException(from, size);
        } else if (from == null) {
            return get("", userId);
        } else {
            Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
            );
            return get("?from={from}&size={size}", userId, parameters);
        }
    }

    public ResponseEntity<Object> searchItems(String text, Integer from, Integer size) {
        if (from == null && size != null || size == null && from != null) {
            throw new PaginationParamException(from, size);
        } else if (from == null) {
            Map<String, Object> parameters = Map.of(
                "text", text);
            return get("/search?text={text}", null, parameters);
        } else {
            Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
            );
            return get("/search?text={text}&from={from}&size={size}", null, parameters);
        }
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}