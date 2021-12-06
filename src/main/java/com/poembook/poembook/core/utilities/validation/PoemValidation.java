package com.poembook.poembook.core.utilities.validation;

import com.poembook.poembook.core.utilities.result.ErrorResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.core.utilities.result.SuccessResult;
import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.CategoryRepo;
import com.poembook.poembook.repository.PoemRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static com.poembook.poembook.constant.CategoryConstant.CATEGORY_NOT_FOUND;
import static com.poembook.poembook.constant.PoemConstant.*;
import static com.poembook.poembook.constant.UserConstant.USER_NOT_FOUND;

@Service
@AllArgsConstructor
public class PoemValidation {
    private static final String POEM_CONTENT_PATTERN = "^.{2,5000}$";
    private static final String POEM_TITLE_PATTERN = "^.{2,50}$";

    private final PoemRepo poemRepo;
    private final CategoryRepo categoryRepo;

    //validations
    public Result validatePoemCreate(User user, String poemTitle, String poemContent, String username, String categoryTitle) {
        if (isPoemTitleNotValid(poemTitle)) {
            return new ErrorResult(POEM_TITLE_NOT_VALID);
        }
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        if (isPoemExist(poemContent)) {
            return new ErrorResult(POEM_ALREADY_EXIST);
        }
        if (isPoemContentNotValid(poemContent)) {
            return new ErrorResult(POEM_CONTENT_INVALID);
        }
        if (!isCategoryExist(categoryTitle)) {
            return new ErrorResult(CATEGORY_NOT_FOUND);
        }
        return new SuccessResult();
    }


    public Result validatePoemUpdate(Poem poem, String poemTitle, String poemContent, String currentUsername, String categoryTitle) {
        if (poem == null) {
            return new ErrorResult(POEM_NOT_FOUND);
        }
        if (isPoemTitleNotValid(poemTitle)) {
            return new ErrorResult(POEM_TITLE_NOT_VALID);
        }
        if (isPoemContentNotValid(poemContent)) {
            return new ErrorResult(POEM_CONTENT_INVALID);
        }
        if (!isCategoryExist(categoryTitle)) {
            return new ErrorResult(CATEGORY_NOT_FOUND);
        }
        return new SuccessResult();
    }

    public Result validateAdminUpdate(Poem poem, String poemTitle, String poemContent, String currentUsername, String categoryTitle) {
        if (poem == null) {
            return new ErrorResult(POEM_NOT_FOUND);
        }
        if (isCategoryExist(categoryTitle)) {
            return new ErrorResult(CATEGORY_NOT_FOUND);
        }
        if (isPoemTitleNotValid(poemTitle)) {
            return new ErrorResult(POEM_TITLE_NOT_VALID);
        }
        if (isPoemContentNotValid(poemContent)) {
            return new ErrorResult(POEM_CONTENT_INVALID);
        }
        return new SuccessResult();
    }

    //helpers
    public boolean isPoemExist(String poemContent) {
        return poemRepo.findByPoemContent(poemContent) != null;
    }

    public boolean isCategoryExist(String categoryTitle) {
        return categoryRepo.findByCategoryTitle(categoryTitle) != null;
    }

    public boolean isPoemTitleNotValid(String poemTitle) {
        Pattern pattern = Pattern.compile(POEM_TITLE_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(poemTitle).find();
    }

    public boolean isPoemContentNotValid(String poemContent) {
        Pattern pattern = Pattern.compile(POEM_CONTENT_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(poemContent).find();


    }


}
