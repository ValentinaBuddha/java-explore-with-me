package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.EventService;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventControllerPrivate {

    private final EventService eventService;

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping
    List<EventShortDto> getEventShortByOwner(@PathVariable Long userId,
                                             @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return eventService.getEventsShortByOwner(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventFullByOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventFullByOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByOwner(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody @Valid UpdateEventUserRequest eventUserRequest) {
        return eventService.updateEventByOwner(userId, eventId, eventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByOwnerEvent(@PathVariable Long userId,
                                                                 @PathVariable Long eventId) {
        return requestService.getRequestsByOwnerEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequests(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.updateStatusRequests(userId, eventId, request);
    }
}
