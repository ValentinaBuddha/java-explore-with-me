package ru.practicum.ewm.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exceptions.NotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
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
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, PageRequest.of(from / size, size)).stream()
                    .map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
        } else {
            return compilationRepository.findAll(pageable).stream()
                    .map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compilationId) {
        return CompilationMapper.toCompilationDto(getCompilation(compilationId));
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
