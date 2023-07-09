package ru.practicum.ewm.compilations;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "pinned")
    Boolean pinned;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compilation_events", joinColumns = @JoinColumn(name = "compilation_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    Set<Event> events;
}
