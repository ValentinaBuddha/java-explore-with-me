package ru.practicum.ewm.events;

import ru.practicum.ewm.categories.CategoryMapper;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.users.UserMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventMapper {
    public static Event toEvent(NewEventDto newEventDto) {
        return new Event(
                newEventDto.getId(),
                newEventDto.getAnnotation(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                LocationMapper.toLocation(newEventDto.getLocation()),
                newEventDto.getPaid(),
                newEventDto.getParticipantLimit(),
                newEventDto.getRequestModeration(),
                newEventDto.getTitle()
        );
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                UserMapper.toUserDtoShort(event.getInitiator()),
                LocationMapper.toLocationDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()
                );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                UserMapper.toUserDtoShort(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
                );
    }
}
