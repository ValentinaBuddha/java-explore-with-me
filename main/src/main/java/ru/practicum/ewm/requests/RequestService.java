package ru.practicum.ewm.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.enums.StatePublic;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.model.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.model.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.model.ParticipationRequest;
import ru.practicum.ewm.users.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestService {
    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request is already exist.");
        }

        if (event.getInitiator().equals(initiator)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can't create request in your event.");
        }

        if (!event.getState().equals(StatePublic.PUBLISHED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event is not published yet.");
        }

        if (!event.getParticipantLimit().equals(0) && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No more space for request.");
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(initiator)
                .status(RequestStatus.PENDING)
                .build();

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    public List<ParticipationRequestDto> getRequestsByOwnerEvent(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        eventRepository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        return requestRepository.findAllByEventId(eventId).stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> getAllRequests(Long userId) {
        return requestRepository.findAllByRequesterId(userId).stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId);
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    public EventRequestStatusUpdateResult updateStatusRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));

        if (!event.getInitiator().equals(initiator)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not initiator.");
        }

        if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No more space for request.");
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndIdIn(eventId, request.getRequestIds()).stream()
                .peek(req -> {
                    if (req.getStatus() != RequestStatus.PENDING) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Can't change status.");
                    }
                    if (request.getStatus() == RequestStatus.REJECTED) {
                        req.setStatus(request.getStatus());
                        rejectedRequests.add(RequestMapper.toParticipationRequestDto(req));
                    }
                    if (event.getConfirmedRequests() < event.getParticipantLimit() && request.getStatus() == RequestStatus.CONFIRMED) {
                        req.setStatus(request.getStatus());
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        confirmedRequests.add(RequestMapper.toParticipationRequestDto(req));
                    } else {
                        req.setStatus(RequestStatus.REJECTED);
                        rejectedRequests.add(RequestMapper.toParticipationRequestDto(req));
                    }
                })
                .collect(Collectors.toList());
        eventRepository.save(event);
        requestRepository.saveAll(requests);
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}
