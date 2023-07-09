package ru.practicum.ewm.requests;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.model.ParticipationRequest;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(
                participationRequest.getCreated(),
                participationRequest.getEvent().getId(),
                participationRequest.getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getStatus()
        );
    }
}
