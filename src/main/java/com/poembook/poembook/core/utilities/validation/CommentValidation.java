package com.poembook.poembook.core.utilities.validation;

import com.poembook.poembook.core.utilities.result.ErrorResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.core.utilities.result.SuccessResult;
import com.poembook.poembook.repository.PoemCommentRepo;
import com.poembook.poembook.repository.PoemRepo;
import com.poembook.poembook.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static com.poembook.poembook.constant.CommentConstant.COMMENT_NOT_FOUND;
import static com.poembook.poembook.constant.CommentConstant.COMMENT_NOT_VALID;
import static com.poembook.poembook.constant.PoemConstant.POEM_NOT_FOUND;
import static com.poembook.poembook.constant.UserConstant.USER_NOT_FOUND;

@AllArgsConstructor
@Service
public class CommentValidation {
    private static final String POEM_COMMENT_PATTERN = "^.{1,200}$";
    private final UserRepo userRepo;
    private final PoemRepo poemRepo;
    private final PoemCommentRepo poemCommentRepo;

    //validations
    public Result validateCreateComment(String poemCommentText, Long poemId, String username) {
        if (poemRepo.findByPoemId(poemId) == null) {
            return new ErrorResult(POEM_NOT_FOUND);
        }
        if (userRepo.findUserByUsername(username) == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        if (isPoemCommentTextNotValid(poemCommentText)) {
            return new ErrorResult(COMMENT_NOT_VALID);
        }
        return new SuccessResult();
    }

    public Result validateUpdateComment(Long poemCommentId, String poemCommentText) {
        if (isPoemCommentExist(poemCommentId)) {
            return new ErrorResult(COMMENT_NOT_FOUND);
        }
        if (isPoemCommentTextNotValid(poemCommentText)) {
            return new ErrorResult(COMMENT_NOT_VALID);
        }
        return new SuccessResult();

    }
    //helpers

    public boolean isPoemCommentTextNotValid(String poemCommentText) {
        poemCommentText = poemCommentText.replace(" ", "");
        Pattern pattern = Pattern.compile(POEM_COMMENT_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(poemCommentText).find();
    }

    public boolean isPoemCommentExist(Long poemCommentId) {
        return poemCommentRepo.findByPoemCommentId(poemCommentId) != null;
    }


}
