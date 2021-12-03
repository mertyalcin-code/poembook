package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.NoticeService;
import com.poembook.poembook.business.abstracts.PoemCommentService;
import com.poembook.poembook.business.abstracts.PoemService;
import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.core.utilities.validation.CommentValidation;
import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.poem.PoemComment;
import com.poembook.poembook.repository.PoemCommentRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.poembook.poembook.constant.CommentConstant.*;
import static com.poembook.poembook.constant.NoticeConstant.NOTICE_MADE_COMMENT;
import static com.poembook.poembook.constant.PoemConstant.POEM_NOT_FOUND;

@Service
@AllArgsConstructor
public class PoemCommentManager implements PoemCommentService {
    private final PoemCommentRepo poemCommentRepo;
    private final PoemService poemService;
    private final UserService userService;
    private final NoticeService noticeService;
    private final CommentValidation validation;

    @Override
    public DataResult<PoemComment> read(Long poemCommentId) {
        PoemComment poemComment = poemCommentRepo.findByPoemCommentId(poemCommentId);
        if (poemComment == null) {
            return new ErrorDataResult<>(COMMENT_NOT_FOUND);
        } else {
            return new SuccessDataResult<>(poemComment, COMMENTS_LISTED);
        }
    }

    @Override
    public Result create(String poemCommentText, Long poemId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!validation.validateCreateComment(poemCommentText, poemId, username).isSuccess()) {
            return new ErrorResult(validation.validateCreateComment(poemCommentText, poemId, username).getMessage());
        }
        PoemComment poemComment = new PoemComment();
        poemComment.setPoemCommentText(poemCommentText);
        poemComment.setUser(userService.findUserByUsername(username).getData());
        poemComment.setPoem(poemService.findById(poemId).getData());
        poemComment.setCommentTime(LocalDateTime.now().atZone(ZoneId.of("UTC")));
        poemComment.setLastCommentUpdateTime(LocalDateTime.now().atZone(ZoneId.of("UTC")));
        poemCommentRepo.save(poemComment);
        poemService.updatePoemCommentCount(poemComment.getPoem());
        if (userService.findUserByUsername(username).getData() != poemService.findById(poemId).getData().getUser()) {
            noticeService.create(userService.findUserByUsername(username).getData().getFirstName() + " " +
                            userService.findUserByUsername(username).getData().getLastName() + NOTICE_MADE_COMMENT
                    , poemService.findById(poemId).getData().getUser().getUsername());
        }
        return new SuccessResult(COMMENT_CREATED);
    }

    @Override
    public Result update(Long poemCommentId, String poemCommentText) {
        PoemComment poemComment = poemCommentRepo.findByPoemCommentId(poemCommentId);
        if (!validation.validateUpdateComment(poemCommentId, poemCommentText).isSuccess()) {
            return new ErrorResult(validation.validateUpdateComment(poemCommentId, poemCommentText).getMessage());
        }
        poemComment.setPoemCommentText(poemCommentText);
        poemComment.setLastCommentUpdateTime(LocalDateTime.now().atZone(ZoneId.of("UTC")));
        poemCommentRepo.save(poemComment);
        return new SuccessResult(COMMENT_UPDATED);
    }

    @Override
    public Result delete(Long commentId) {
        PoemComment deletedComment = poemCommentRepo.findByPoemCommentId(commentId);
        if (deletedComment == null) {
            return new ErrorResult(COMMENT_NOT_FOUND);
        }
        Poem poem = deletedComment.getPoem();
        poemCommentRepo.delete(deletedComment);
        poemService.updatePoemCommentCount(poem);
        return new SuccessResult(COMMENT_DELETED);
    }

    @Override
    public void deletePoemsAllComments(Long poemId) {
        List<PoemComment> comments = poemCommentRepo.findAllByPoem(poemService.findById(poemId).getData());
        if (comments.size() > 0) {
            poemCommentRepo.deleteAll(comments);
        }
    }

    @Override
    public DataResult<List<PoemComment>> listPoemsComments(Long poemId) {
        Poem poem = poemService.findById(poemId).getData();
        if (poem == null) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        }
        List<PoemComment> comments = poemCommentRepo.findAllByPoem(poem);
        if (comments.size() < 1) {
            return new ErrorDataResult<>(COMMENT_NOT_FOUND);
        } else {
            return new SuccessDataResult<>(COMMENTS_LISTED);
        }
    }


}
