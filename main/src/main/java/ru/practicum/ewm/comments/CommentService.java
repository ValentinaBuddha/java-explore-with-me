package ru.practicum.ewm.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentDto addComment(Long userId, NewCommentDto newCommentDto) {
        return null;
    }

    public CommentDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        return null;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size) {
        return null;
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Integer from, Integer size) {
        return null;
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        return null;
    }

    public void deleteComment(Long commentId) {
    }
}
