package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.CategoryService;
import com.poembook.poembook.business.abstracts.LoggerService;
import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.constant.CategoryConstant;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.core.utilities.validation.CategoryValidation;
import com.poembook.poembook.entities.category.Category;
import com.poembook.poembook.repository.CategoryRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.poembook.poembook.constant.CategoryConstant.*;
import static com.poembook.poembook.constant.LoggerConstant.*;
import static com.poembook.poembook.constant.enumaration.Log.LOG_CATEGORY_CREATE;
import static com.poembook.poembook.constant.enumaration.Log.LOG_CATEGORY_UPDATE;

@Service
@AllArgsConstructor
public class CategoryManager implements CategoryService {
    private final CategoryRepo categoryRepo;
    private final UserService userService;
    private final CategoryValidation validation;
    private final LoggerService logger;

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
        if (!validation.validateCategoryCreate(categoryTitle).isSuccess()) {
            return new ErrorResult(validation.validateCategoryCreate(categoryTitle).getMessage());
        }
        Category category = new Category();
        category.setCreatorUsername(userService.findUserByUsername(currentUsername).getData().getUsername());
        category.setCategoryTitle(categoryTitle);
        category.setCreationDate(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));
        category.setActive(isActive);
        categoryRepo.save(category);
        logger.log(LOG_CATEGORY_CREATE.toString(),
                CATEGORY_CREATED_LOG + categoryTitle + PROCESS_OWNER + SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return new SuccessResult(CategoryConstant.CATEGORY_CREATED);
    }

    @Override
    public Result update(int categoryId, String newCategoryTitle, String username, boolean isActive) {
        Category category = categoryRepo.findByCategoryId(categoryId);
        newCategoryTitle = StringUtils.capitalize(StringUtils.lowerCase(newCategoryTitle));
        if (validation.isCategoryTitleNotValid(newCategoryTitle)) {
            return new ErrorResult(CATEGORY_TITLE_NOT_VALID);
        }
        String oldTitle = category.getCategoryTitle();
        category.setUpdateUsername(userService.findUserByUsername(username).getData().getUsername());
        category.setCategoryTitle(newCategoryTitle);
        category.setLastUpdateDate(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));
        category.setActive(isActive);
        categoryRepo.save(category);
        logger.log(LOG_CATEGORY_UPDATE.toString(),
                oldTitle + CATEGORY_UPDATED_LOG + newCategoryTitle + PROCESS_OWNER + SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return new SuccessResult(CATEGORY_UPDATED);
    }

    @Override
    public Result delete(String categoryTitle) { //do not work if there are poems in this category
        categoryTitle = StringUtils.capitalize(StringUtils.lowerCase(categoryTitle));
        Category category = categoryRepo.findByCategoryTitle(categoryTitle);
        if (category == null) {
            return new ErrorResult(CATEGORY_NOT_FOUND);
        }
        categoryRepo.delete(category);
        logger.log(LOG_CATEGORY_CREATE.toString(),
                CATEGORY_DELETED_LOG + categoryTitle + PROCESS_OWNER + SecurityContextHolder.getContext().getAuthentication().getName()
        );
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
