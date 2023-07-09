package ru.practicum.ewm.events;

import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.events.enums.StatePublic;
import ru.practicum.ewm.locations.Location;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.users.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation", length = 2000, nullable = false)
    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "confirmed_request", nullable = false)
    Integer confirmedRequests;

    @Column(name = "created_on", nullable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime createdOn;

    @Column(length = 7000, nullable = false)
    String description;

    @Column(name = "event_date", nullable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    Location location;

    @Column(nullable = false)
    Boolean paid;

    @Column(nullable = false)
    Integer participantLimit;

    @Column(name = "published_on", columnDefinition = "TIMESTAMP")
    LocalDateTime publishedOn;

    @Column(nullable = false)
    Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    StatePublic state;

    @Column(length = 120, nullable = false)
    String title;

    @Column
    Long views;
}
