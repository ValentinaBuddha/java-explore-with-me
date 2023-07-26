package ru.practicum.ewm.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.events.EventMapper;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.requests.RequestRepository;
import ru.practicum.ewm.requests.dto.ConfirmedRequests;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.UserMapper;
import ru.practicum.ewm.users.UserRepository;
import ru.practicum.ewm.users.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.events.model.State.PUBLISHED;
import static ru.practicum.ewm.requests.model.RequestStatus.CONFIRMED;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);
        if (event.getState() != PUBLISHED) {
            throw new ValidationException("Comments are available only for published events.");
        }
        Comment comment = commentRepository.save(CommentMapper.toComment(newCommentDto, author, event));
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        EventShortDto eventShort = EventMapper.toEventShortDto(event,
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id=" + commentId + " was not found"));
        if (comment.getEvent() != event) {
            throw new ValidationException("This comment is for other event.");
        }
        comment.setText(newCommentDto.getText());
        comment.setEdited(LocalDateTime.now());
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        EventShortDto eventShort = EventMapper.toEventShortDto(event,
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size) {
        User author = checkAndGetUser(userId);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, PageRequest.of(from / size, size));
        List<Long> eventIds = comments.stream().map(comment -> comment.getEvent().getId()).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(eventIds, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        List<CommentDto> result = new ArrayList<>();
        for (Comment c : comments) {
            Long eventId  = c.getEvent().getId();
            EventShortDto eventShort = EventMapper.toEventShortDto(c.getEvent(), confirmedRequests.get(eventId));
            result.add(CommentMapper.toCommentDto(c, userShort, eventShort));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long eventId, Integer from, Integer size) {
        Event event = checkAndGetEvent(eventId);
        EventShortDto eventShort = EventMapper.toEventShortDto(event,
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        return commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size))
                .stream()
                .map(c -> CommentMapper.toCommentDto(c, UserMapper.toUserShortDto(c.getAuthor()), eventShort))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        Comment comment = checkAndGetComment(commentId);
        UserShortDto userShort = UserMapper.toUserShortDto(comment.getAuthor());
        EventShortDto eventShort = EventMapper.toEventShortDto(comment.getEvent(),
                requestRepository.countByEventIdAndStatus(comment.getEvent().getId(), CONFIRMED));
        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    public void deleteComment(Long userId, Long commentId) {
        User author = checkAndGetUser(userId);
        Comment comment = checkAndGetComment(commentId);
        if (comment.getAuthor() != author) {
            throw new ValidationException("Only author can delete the comment.");
        }
        commentRepository.deleteById(commentId);
    }

    public void deleteComment(Long commentId) {
        checkAndGetComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private User checkAndGetUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    private Event checkAndGetEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private Comment checkAndGetComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id=" + commentId + " was not found"));
    }
}
