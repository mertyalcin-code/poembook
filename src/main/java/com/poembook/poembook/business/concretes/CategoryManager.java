package com.poembook.poembook.business.concretes;

import com.poembook.poembook.auth.configuration.UserDetailsServiceImpl;
import com.poembook.poembook.business.abstracts.CategoryService;
import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.constant.CategoryConstant;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.core.utilities.validation.CategoryValidation;
import com.poembook.poembook.entities.category.Category;
import com.poembook.poembook.repository.CategoryRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.poembook.poembook.constant.CategoryConstant.*;

@Service
@AllArgsConstructor
public class CategoryManager implements CategoryService {
    private final CategoryRepo categoryRepo;
    private final UserService userService;
    private final CategoryValidation categoryValidation;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public DataResult<Category> read(String categoryTitle) {
        categoryTitle = StringUtils.capitalize(StringUtils.lowerCase(categoryTitle));
        Category category = categoryRepo.findByCategoryTitle(categoryTitle);
        if (category == null) {
            return new ErrorDataResult<>(CATEGORY_NOT_FOUND);
        }
        return new SuccessDataResult<>(category, CATEGORY_LIST_SUCCESS);
    }

    @Override
    public Result create(String categoryTitle, String currentUsername, boolean isActive) {
        categoryTitle = StringUtils.capitalize(StringUtils.lowerCase(categoryTitle));
        if (categoryValidation.isCategoryExist(categoryTitle)) {
            return new ErrorResult(CATEGORY_TITLE_EXIST);
        }
        if (!categoryValidation.isCategoryTitleValid(categoryTitle)) {
            return new ErrorResult(CATEGORY_TITLE_NOT_VALID);
        }
        Category category = new Category();
        category.setCreatorUsername(userService.findUserByUsername(currentUsername).getData().getUsername());
        category.setCategoryTitle(categoryTitle);
        category.setCreationDate(new Date());
        category.setActive(isActive);
        categoryRepo.save(category);
        return new SuccessResult(CategoryConstant.CATEGORY_CREATED);
    }

    @Override
    public Result update(int categoryId, String newCategoryTitle, String username, boolean isActive) {
        Category category = categoryRepo.findByCategoryId(categoryId);
        if (!categoryValidation.isCategoryTitleValid(newCategoryTitle)) {
            return new ErrorResult(CATEGORY_TITLE_NOT_VALID);
        }
        category.setUpdateUsername(userService.findUserByUsername(username).getData().getUsername());
        category.setCategoryTitle(newCategoryTitle);
        category.setLastUpdateDate(new Date());
        category.setActive(isActive);
        categoryRepo.save(category);
        return new SuccessResult(CATEGORY_UPDATED);
    }

    @Override
    public Result delete(String categoryTitle) {
        categoryTitle = StringUtils.capitalize(StringUtils.lowerCase(categoryTitle));
        Category category = categoryRepo.findByCategoryTitle(categoryTitle);
        if (category == null) {
            return new ErrorResult(CATEGORY_NOT_FOUND);
        }
        categoryRepo.delete(category);
        return new SuccessResult(CATEGORY_DELETED);
    }

    @Override
    public DataResult<List<Category>> listAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        if (categories.size() < 1) {
            return new ErrorDataResult<>(CATEGORY_NOT_FOUND);
        } else {
            return new SuccessDataResult<>(categories, CATEGORY_LIST_SUCCESS);
        }
    }

    @Override
    public DataResult<List<Category>> listAllActiveCategories() {
        List<Category> categories = categoryRepo.findAllByIsActive(true);
        if (categories.size() < 1) {
            return new ErrorDataResult<>(CATEGORY_NOT_FOUND);
        } else {
            return new SuccessDataResult<>(categories, CATEGORY_LIST_SUCCESS);
        }
    }

    @Override
    public DataResult<Category> findCategoryByCategoryTitle(String categoryTitle) {
        categoryTitle = StringUtils.capitalize(StringUtils.lowerCase(categoryTitle));
        Category category = categoryRepo.findByCategoryTitle(categoryTitle);
        if (category == null) {
            return new ErrorDataResult<>(CATEGORY_NOT_FOUND);
        }
        return new SuccessDataResult<>(category, CATEGORY_LIST_SUCCESS);
    }


    @Override
    public DataResult<Category> findCategoryById(int categoryId) {
        Category category = categoryRepo.findByCategoryId(categoryId);
        if (category == null) {
            return new ErrorDataResult<>(CATEGORY_NOT_FOUND);
        } else {
            return new SuccessDataResult<>(category, CATEGORY_LIST_SUCCESS);
        }

    }
}
