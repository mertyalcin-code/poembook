package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.category.Category;

import java.util.List;

public interface CategoryService {
    DataResult<Category> read(String categoryTitle);

    Result create(String categoryTitle, String username, boolean isActive);

    Result update(int categoryId, String newCategoryTitle, String username, boolean isActive);

    Result delete(String categoryTitle);

    DataResult<List<Category>> listAllCategories();

    DataResult<List<Category>> listAllActiveCategories();

    DataResult<Category> findCategoryByCategoryTitle(String categoryTitle);

    DataResult<Category> findCategoryById(int categoryId);

}
