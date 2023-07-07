package ru.practicum.model;

import lombok.experimental.UtilityClass;
import ru.practicum.EndpointHitDto;

@UtilityClass
public class EndpointHitMapper {
    public EndpointHit toHit(EndpointHitDto hit) {
        return new EndpointHit(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }
}
