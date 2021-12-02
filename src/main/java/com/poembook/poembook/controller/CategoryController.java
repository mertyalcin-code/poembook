package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.CategoryService;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.entities.category.Category;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
@PreAuthorize("hasAuthority('poet') or hasAuthority('editor') or hasAuthority('admin') or hasAuthority('superAdmin')")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/list")
    public DataResult<List<Category>> listAllCategories() {
        return categoryService.listAllCategories();
    }

    @GetMapping("/list-active")
    public DataResult<List<Category>> listAllActiveCategories() {
        return categoryService.listAllActiveCategories();
    }

    @GetMapping("/category-title/{categoryTitle}")
    public DataResult<Category> readCategoryByCategoryTitle(@PathVariable String categoryTitle) {
        return categoryService.read(categoryTitle);
    }

    @GetMapping("/category-id/{id}")
    public DataResult<Category> readCategoryById(@PathVariable int id) {
        return categoryService.findCategoryById(id);
    }


}
