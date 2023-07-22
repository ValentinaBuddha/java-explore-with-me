package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.events.model.Event;

@AllArgsConstructor
@Getter
@Setter
public class ConfirmedRequests {
    private Long count;
    private Event event;
}
