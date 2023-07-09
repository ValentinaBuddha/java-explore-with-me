package ru.practicum.ewm.categories;

import ru.practicum.ewm.categories.dto.CategoryDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryMapper {
    public Category toCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName()
        );
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
