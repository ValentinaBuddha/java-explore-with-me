package ru.practicum.ewm.locations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull
    private Float lat;

    @NotNull
    private Float lon;
}
