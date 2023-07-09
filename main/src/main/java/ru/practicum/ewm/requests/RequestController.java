package ru.practicum.ewm.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getAllRequests(@PathVariable Long userId) {
        return requestService.getAllRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
