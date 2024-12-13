package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.UserRepository;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Comment commentDtoToComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setAuthorName(userRepository.findByName(commentDto.getAuthorName()).orElse(null));
        comment.setCommentedItem(itemRepository.findByName(commentDto.getCommentedItem()).orElse(null));
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
