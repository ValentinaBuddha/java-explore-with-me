package ru.practicum.ewm.categories.controller;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryControllerAdmin {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }
}
