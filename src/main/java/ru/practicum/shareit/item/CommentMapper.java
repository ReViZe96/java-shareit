package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class CommentMapper {

    public Comment commentDtoToComment(CommentDto commentDto, Item commentedItem, User author) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setAuthorName(author);
        comment.setCommentedItem(commentedItem);
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public CommentDto commentToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthorName().getName());
        commentDto.setCommentedItem(comment.getCommentedItem().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

}
