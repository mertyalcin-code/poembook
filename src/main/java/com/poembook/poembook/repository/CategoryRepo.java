package com.poembook.poembook.repository;

import com.poembook.poembook.entities.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category, Integer> {
    Category findByCategoryTitle(String categoryTitle);

    Category findByCategoryId(int categoryId);

    List<Category> findAllByIsActive(boolean status);
}
