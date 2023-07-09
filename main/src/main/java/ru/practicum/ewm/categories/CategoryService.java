package ru.practicum.ewm.categories;

import ru.practicum.ewm.categories.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final  CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        int pageNumber = (int) Math.ceil((double) from / size);
        Pageable pageable = PageRequest.of(pageNumber, size);

        return categoryRepository.findAll(pageable).map(CategoryMapper::toCategoryDto).getContent();
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        return CategoryMapper.toCategoryDto(
                categoryRepository
                        .findById(categoryId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."))
        );
    }

    public CategoryDto addCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Name is already used.");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));
        if (categoryRepository.existsByName(categoryDto.getName()) && !categoryDto.getName().equals(category.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Name is already used.");
        }
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        }
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category not found.");
        }
        categoryRepository.deleteById(catId);
    }
}
