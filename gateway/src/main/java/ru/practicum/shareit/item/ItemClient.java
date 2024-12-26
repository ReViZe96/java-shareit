package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    public ResponseEntity<Object> getAllItems(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> getItemById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> addItem(ItemDto newItem, Long ownerId) {
        return post("", ownerId, newItem);
    }

    public ResponseEntity<Object> editItem(Long itemId, ItemDto editedItem, Long ownerId) {
        return patch("/" + itemId, ownerId, editedItem);
    }

    public ResponseEntity<Object> findItems(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("", parameters);
    }

    public ResponseEntity<Object> addComment(Long itemId, CommentDto newComment, Long authorId) {
        return post("/" + itemId + "/comment", authorId, newComment);
    }

}
