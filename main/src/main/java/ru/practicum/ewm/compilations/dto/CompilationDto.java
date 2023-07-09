package ru.practicum.ewm.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private List<EventShortDto> events;

    private Long id;

    private Boolean pinned;

    private String title;
}
