package ru.practicum.ewm.events;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.categories.CategoryService;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.enums.StateAdmin;
import ru.practicum.ewm.events.enums.StatePrivate;
import ru.practicum.ewm.events.enums.StatePublic;
import ru.practicum.ewm.locations.LocationRepository;
import ru.practicum.ewm.users.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventService {
    EventRepository eventRepository;

    UserRepository userRepository;

    CategoryRepository categoryRepository;

    CategoryService categoryService;

    LocationRepository locationRepository;

    HitClient hitClient;


    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
        Location location = LocationMapper.toLocation(newEventDto.getLocation());
        location = locationRepository.existsByLatAndLon(location.getLat(), location.getLon())
                ? locationRepository.findByLatAndLon(location.getLat(), location.getLon()) : locationRepository.save(location);
        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(StatePublic.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0L);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventFullByOwner(Long userId, Long eventId) {
        return EventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId));
    }

    @Override
    public List<EventShortDto> getEventsShortByOwner(Long userId, Integer from, Integer size) {
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);
        return eventRepository.findAllByInitiatorId(userId, pageable).map(EventMapper::toEventShortDto).getContent();
    }

    @Override
    public EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event.getState() == StatePublic.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Can't change, because it already Published.");
        }
        if (eventUserRequest.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategory(categoryService.getCategoryById(eventUserRequest.getCategory())));
        }
        if (eventUserRequest.getAnnotation() != null) {
            event.setAnnotation(eventUserRequest.getAnnotation());
        }
        if (eventUserRequest.getDescription() != null) {
            event.setDescription(eventUserRequest.getDescription());
        }
        if (eventUserRequest.getEventDate() != null) {
            event.setEventDate(eventUserRequest.getEventDate());
        }
        if (eventUserRequest.getLocation() != null) {
            Location location = LocationMapper.toLocation(eventUserRequest.getLocation());
            location = locationRepository.existsByLatAndLon(location.getLat(), location.getLon())
                    ? locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                    : locationRepository.save(location);
            event.setLocation(location);
        }
        if (eventUserRequest.getPaid() != null) {
            event.setPaid(eventUserRequest.getPaid());
        }
        if (eventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUserRequest.getParticipantLimit());
        }
        if (eventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(eventUserRequest.getRequestModeration());
        }
        if (eventUserRequest.getTitle() != null) {
            event.setTitle(eventUserRequest.getTitle());
        }
        if (eventUserRequest.getStateAction() != null) {
            StatePrivate statePrivate = StatePrivate.valueOf(eventUserRequest.getStateAction());
            if (statePrivate.equals(StatePrivate.SEND_TO_REVIEW)) {
                event.setState(StatePublic.PENDING);
            } else if (statePrivate.equals(StatePrivate.CANCEL_REVIEW)) {
                event.setState(StatePublic.CANCELED);
            }
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        if (eventAdminRequest.getStateAction() != null) {
            StateAdmin stateAdmin = StateAdmin.valueOf(eventAdminRequest.getStateAction());
            if (!event.getState().equals(StatePublic.PENDING) && stateAdmin.equals(StateAdmin.PUBLISH_EVENT)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Event is not Pending");
            }
            if (event.getState().equals(StatePublic.PUBLISHED) && stateAdmin.equals(StateAdmin.REJECT_EVENT)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Event is not Published yet");
            }
        }
        if (eventAdminRequest.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategory(categoryService.getCategoryById(eventAdminRequest.getCategory())));
        }
        if (eventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(eventAdminRequest.getAnnotation());
        }
        if (eventAdminRequest.getDescription() != null) {
            event.setDescription(eventAdminRequest.getDescription());
        }
        if (eventAdminRequest.getEventDate() != null) {
            event.setEventDate(eventAdminRequest.getEventDate());
        }
        if (eventAdminRequest.getLocation() != null) {
            Location location = LocationMapper.toLocation(eventAdminRequest.getLocation());
            location = locationRepository.existsByLatAndLon(location.getLat(), location.getLon())
                    ? locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                    : locationRepository.save(location);
            event.setLocation(location);
        }
        if (eventAdminRequest.getPaid() != null) {
            event.setPaid(eventAdminRequest.getPaid());
        }
        if (eventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventAdminRequest.getParticipantLimit());
        }
        if (eventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(eventAdminRequest.getRequestModeration());
        }
        if (eventAdminRequest.getTitle() != null) {
            event.setTitle(eventAdminRequest.getTitle());
        }
        if (eventAdminRequest.getStateAction() != null) {
            StateAdmin statePrivate = StateAdmin.valueOf(eventAdminRequest.getStateAction());
            if (statePrivate.equals(StateAdmin.PUBLISH_EVENT)) {
                event.setState(StatePublic.PUBLISHED);
            } else if (statePrivate.equals(StateAdmin.REJECT_EVENT)) {
                event.setState(StatePublic.CANCELED);
            }
        }
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Boolean onlyAvailable, String sort, Integer from,
                                         Integer size, HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong timestamps of START or END.");
        }
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);

        Specification<Event> specification = Specification.where(null);

        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), StatePublic.PUBLISHED));

        List<Event> resultEvents = eventRepository.findAll(specification, pageable).getContent();
        setViewsOfEvents(resultEvents);

        return resultEvents.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong timestamps of START or END.");
        }
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);

        Specification<Event> specification = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("initiator").get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("state").as(String.class).in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("category").get("id").in(categories));
        }

        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        return eventRepository.findAll(specification, pageable).map(EventMapper::toEventFullDto).getContent();
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        if (event.getState() != StatePublic.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found.");
        }
        setViewsOfEvents(List.of(event));
        event.setViews(event.getViews() + 1);
        return EventMapper.toEventFullDto(event);
    }

    private void setViewsOfEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        List<StatsDto> viewStatsList = hitClient.getStats("2000-01-01 00:00:00", "2100-01-01 00:00:00", uris, false);

        for (Event event : events) {
            StatsDto currentViewStats = viewStatsList.stream()
                    .filter(statsDto -> {
                        Long eventIdOfViewStats = Long.parseLong(statsDto.getUri().substring("/events/".length()));
                        return eventIdOfViewStats.equals(event.getId());
                    })
                    .findFirst()
                    .orElse(null);

            Long views = (currentViewStats != null) ? currentViewStats.getHits() : 0;
            event.setViews(views);
        }
        eventRepository.saveAll(events);
    }
}
