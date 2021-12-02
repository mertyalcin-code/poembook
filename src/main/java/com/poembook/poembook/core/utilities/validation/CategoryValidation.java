package com.poembook.poembook.core.utilities.validation;

import com.poembook.poembook.core.utilities.result.ErrorResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.core.utilities.result.SuccessResult;
import com.poembook.poembook.repository.CategoryRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static com.poembook.poembook.constant.CategoryConstant.CATEGORY_TITLE_EXIST;
import static com.poembook.poembook.constant.CategoryConstant.CATEGORY_TITLE_NOT_VALID;

@Service
@AllArgsConstructor
public class CategoryValidation {
    private static final String CATEGORY_TITLE_PATTERN = "^...{1,30}$";
    private final CategoryRepo categoryRepo;

    //validations
    public Result validateCategoryCreate(String categoryTitle) {
        if (isCategoryExist(categoryTitle)) {
            return new ErrorResult(CATEGORY_TITLE_EXIST);
        }
        if (isCategoryTitleNotValid(categoryTitle)) {
            return new ErrorResult(CATEGORY_TITLE_NOT_VALID);
        }
        return new SuccessResult();
    }

    //helpers
    public boolean isCategoryExist(String categoryTitle) {
        return categoryRepo.findByCategoryTitle(categoryTitle) != null;
    }

    public boolean isCategoryTitleNotValid(String categoryTitle) {
        Pattern pattern = Pattern.compile(CATEGORY_TITLE_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(categoryTitle).find();
    }


}

