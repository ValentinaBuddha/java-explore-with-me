package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000)
    String annotation;

    Long category;

    @Size(min = 20, max = 7000)
    String description;

    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    LocationDto location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    String stateAction;

    @Size(min = 3, max = 120)
    String title;
}
