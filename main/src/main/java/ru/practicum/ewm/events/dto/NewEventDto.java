package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    Long id;

    @Size(min = 20, max = 2000)
    @NotBlank
    String annotation;

    @NotNull
    Long category;

    @Size(min = 20, max = 7000)
    @NotBlank
    String description;

    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @NotNull
    LocationDto location;

    Boolean paid = false;

    Integer participantLimit = 0;

    Boolean requestModeration = true;

    @Size(min = 3, max = 120)
    @NotBlank
    String title;
}
