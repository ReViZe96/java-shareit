package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok(itemService.getAllItems(ownerId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemById(itemId));
    }

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemDto newItem,
                                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.addItem(newItem, ownerId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> editItem(@PathVariable Long itemId,
                                            @RequestBody ItemDto editedItem,
                                            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok(itemService.editItem(itemId, editedItem, ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> findItems(@RequestParam("text") String text) {
        return ResponseEntity.ok(itemService.findItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long itemId,
                                                 @RequestBody CommentDto newComment,
                                                 @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.addComment(itemId, newComment, authorId));
    }

}
