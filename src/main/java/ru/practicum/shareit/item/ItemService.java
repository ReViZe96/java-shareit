package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItems(Long ownerId);

    ItemDto getItemById(Long itemId);

    ItemDto addItem(ItemDto newItem, Long ownerId);

    ItemDto editItem(Long itemId, ItemDto editedItem, Long ownerId);

    List<ItemDto> findItems(String text);

    CommentDto addComment(Long itemId, CommentDto newComment, Long authorId);

}
