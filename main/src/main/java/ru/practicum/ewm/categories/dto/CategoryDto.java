package ru.practicum.ewm.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Long id;

    @Size(min = 1, max = 50)
    @NotBlank
    private String name;
}
