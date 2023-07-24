package ru.practicum.ewm.comments;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.dto.UserShortDto;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment toComment(NewCommentDto newCommentDto, Event event, User author) {
        Comment comment = new Comment();
        comment.setEvent(event);
        comment.setAuthor(author);
        comment.setText(newCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public CommentDto toCommentDto(Comment comment, EventShortDto event, UserShortDto author) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                event,
                author,
                comment.getCreated(),
                comment.getEdit()
        );
    }
}
