package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.NoticeService;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.entities.notification.Notice;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.NoticeRepo;
import com.poembook.poembook.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.poembook.poembook.constant.NoticeConstant.*;
import static com.poembook.poembook.constant.UserConstant.USER_NOT_FOUND;

@Service
@AllArgsConstructor
public class NoticeManager implements NoticeService {
    private NoticeRepo noticeRepo;
    private UserRepo userRepo;


    @Override
    public Result create(String notice,String username) {
        if(notice==null){
            return new ErrorResult(NOTICE_CANNOT_BE_NULL);
        }
        User user = userRepo.findUserByUsername(username);
        if(user==null){
            return new ErrorResult(USER_NOT_FOUND);
        }
        Notice newNotice = new Notice();
        newNotice.setNoticeText(notice);
        newNotice.setUser(user);
        newNotice.setNoticeTime(new Date());
        noticeRepo.save(newNotice);
        return new SuccessResult(NOTICE_CREATED);
    }

    @Override
    public Result delete(Long id) {
       Notice deletedNotice = noticeRepo.findByNoticeId(id);
        if (deletedNotice ==null){
            return new ErrorResult(NOTICE_NOT_FOUND);
        }
        noticeRepo.delete(deletedNotice);
        return new SuccessResult(NOTICE_DELETED);
    }

    @Override
    public Result deleteAll(String username) {
        User user = userRepo.findUserByUsername(username);
        if(user==null){
            return new ErrorResult(USER_NOT_FOUND);
        }
        List<Notice> notices = noticeRepo.findAllByUser(user);
        if(notices ==null){
            return new ErrorResult((NOTICE_NOT_FOUND));
        }
        noticeRepo.deleteAll(notices);
        return new SuccessResult(NOTICE_DELETED_ALL);
    }

    @Override
    public DataResult<List<Notice>> listAll(String username) {
        User user = userRepo.findUserByUsername(username);
        if(user==null){
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        List<Notice> notices = noticeRepo.findAllByUser(user);
        if(notices ==null){
            return new ErrorDataResult<>((NOTICE_NOT_FOUND));
        }
        return new SuccessDataResult<>(notices,NOTICE_LISTED);
    }
}
