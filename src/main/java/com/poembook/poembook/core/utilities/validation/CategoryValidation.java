package com.poembook.poembook.core.utilities.validation;

import com.poembook.poembook.repository.CategoryRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class CategoryValidation {
    private static final String CATEGORY_TITLE_PATTERN = "^\\w{2,50}$";
    private final CategoryRepo categoryRepo;

    public boolean isCategoryExist(String categoryTitle) {
        return categoryRepo.findByCategoryTitle(categoryTitle) != null;
    }

    public boolean isCategoryTitleValid(String categoryTitle) {
        Pattern pattern = Pattern.compile(CATEGORY_TITLE_PATTERN, Pattern.CASE_INSENSITIVE);
        //  return pattern.matcher(categoryTitle).find();
        return true;
    }

}

