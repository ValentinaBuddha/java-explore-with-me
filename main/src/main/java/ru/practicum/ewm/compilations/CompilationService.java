package ru.practicum.ewm.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.events.EventMapper;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.requests.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.requests.model.RequestStatus.CONFIRMED;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
        }
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        if (compilation.getEvents() != null) {
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(event,
                            requestRepository.countByEventIdAndStatus(event.getId(), CONFIRMED)))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation) {
        Compilation compilation = getCompilation(compId);
        if (updateCompilation.getEvents() != null) {
            Set<Event> events = updateCompilation.getEvents().stream().map(id -> {
                Event event = new Event();
                event.setId(id);
                return event;
            }).collect(Collectors.toSet());
            compilation.setEvents(events);
        }
        if (updateCompilation.getPinned() != null) {
            compilation.setPinned(updateCompilation.getPinned());
        }
        String title = updateCompilation.getTitle();
        if (title != null && !title.isBlank()) {
            compilation.setTitle(title);
        }
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        if (compilation.getEvents() != null) {
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(event,
                            requestRepository.countByEventIdAndStatus(event.getId(), CONFIRMED)))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned != null) {
            List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
            List<CompilationDto> result = new ArrayList<>();
            for (Compilation compilation : compilations) {
                CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
                if (compilation.getEvents() != null) {
                    compilationDto.setEvents(compilation.getEvents().stream()
                            .map(event -> EventMapper.toEventShortDto(event,
                                    requestRepository.countByEventIdAndStatus(event.getId(), CONFIRMED)))
                            .collect(Collectors.toList()));
                }
                result.add(compilationDto);
            }
            return result;
        } else {
            List<Compilation> compilations = compilationRepository.findAll(pageable).getContent();
            List<CompilationDto> result = new ArrayList<>();
            for (Compilation compilation : compilations) {
                CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
                if (compilation.getEvents() != null) {
                    compilationDto.setEvents(compilation.getEvents().stream()
                            .map(event -> EventMapper.toEventShortDto(event,
                                    requestRepository.countByEventIdAndStatus(event.getId(), CONFIRMED)))
                            .collect(Collectors.toList()));
                }
                result.add(compilationDto);
            }
            return result;
        }
    }

    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compilationId) {
        Compilation compilation = getCompilation(compilationId);
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
        if (compilation.getEvents() != null) {
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(event,
                            requestRepository.countByEventIdAndStatus(event.getId(), CONFIRMED)))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    public void deleteCompilation(Long compilationId) {
        getCompilation(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    private Compilation getCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new NotFoundException("Compilation id=" + compilationId + " not found"));
    }
}
