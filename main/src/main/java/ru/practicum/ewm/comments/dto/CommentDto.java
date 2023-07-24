package ru.practicum.ewm.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.users.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.ewm.util.DateConstant.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private EventShortDto event;
    private UserShortDto author;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime edit;
}
