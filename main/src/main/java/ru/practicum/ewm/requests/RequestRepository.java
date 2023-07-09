package ru.practicum.ewm.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.requests.model.ParticipationRequest;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    ParticipationRequest findByIdAndRequesterId(Long requestId, Long userId);

    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    List<ParticipationRequest> findAllByEventIdAndIdIn(Long eventId, List<Long> requestId);
}
