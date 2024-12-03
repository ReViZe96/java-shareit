package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
public class CommentDto {

    private String text;
    private User authorName;
    private Item commentedItem;

}
