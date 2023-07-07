package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Идентификатор сервиса для которого записывается информация
    @Column(nullable = false)
    private String app;

    //URI для которого был осуществлен запрос
    @Column(nullable = false)
    private String uri;

    //IP-адрес пользователя, осуществившего запрос
    @Column(nullable = false, length = 15)
    private String ip;

    //Дата и время, когда был совершен запрос к эндпоинту
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
