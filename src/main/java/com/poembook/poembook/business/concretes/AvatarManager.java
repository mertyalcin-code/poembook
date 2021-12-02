package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.AvatarService;
import com.poembook.poembook.business.abstracts.LoggerService;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.core.utilities.service.cloudinary.CloudService;
import com.poembook.poembook.entities.users.Avatar;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.AvatarRepo;
import com.poembook.poembook.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static com.poembook.poembook.constant.AvatarManagerConstant.*;
import static com.poembook.poembook.constant.LoggerConstant.*;
import static com.poembook.poembook.constant.UserConstant.USER_NOT_FOUND;
import static com.poembook.poembook.constant.enumaration.Log.LOG_AVATAR_CREATE;
import static com.poembook.poembook.constant.enumaration.Log.LOG_AVATAR_DELETE;

@Service
@AllArgsConstructor
public class AvatarManager implements AvatarService {
    private AvatarRepo avatarRepo;
    private CloudService cloudService;
    private UserRepo userRepo;
    private LoggerService logger;


    @Override
    public DataResult<List<Avatar>> getAll() {
        return new SuccessDataResult<>(this.avatarRepo.findAll(), AVATAR_LISTED);
    }

    @Override
    public DataResult<Avatar> getById(int id) {
        if (avatarRepo.findByAvatarId(id) == null) {
            return new ErrorDataResult<>(AVATAR_NOT_FOUND);
        }
        return new SuccessDataResult<>(this.avatarRepo.findByAvatarId(id), AVATAR_LISTED);
    }

    @Override
    public DataResult<Avatar> getByUserId(Long id) {
        if (userRepo.findUserByUserId(id) == null) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        return new SuccessDataResult<>(this.avatarRepo.findByUser(userRepo.findUserByUserId(id)), AVATAR_LISTED);
    }

    @Override
    public Result add(MultipartFile multipartFile, String username) {

        DataResult<Map<String, String>> result = this.cloudService.upload(multipartFile);
        if (!result.isSuccess()) {
            return new ErrorResult(result.getMessage());
        }
        User user = this.userRepo.findUserByUsername(username);
        System.out.println(user.getUsername());
        Avatar avatar = new Avatar();
        avatar.setImageUrl(result.getData().get("url"));
        avatar.setPublic_id(result.getData().get("public_id"));
        avatar.setUploadedDate(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));
        user.setAvatar(avatar);
        userRepo.save(user);
        logger.log(LOG_AVATAR_CREATE.toString(),
                username + AVATAR_CREATED_LOG + avatar.getImageUrl() + PROCESS_OWNER + SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return new SuccessResult(AVATAR_CREATED);
    }

    @Override
    public Result delete(int id) throws IOException {
        String public_id = this.avatarRepo.findByAvatarId(id).getPublic_id();
        this.cloudService.delete(public_id);
        this.avatarRepo.deleteByAvatarId(id);
        logger.log(LOG_AVATAR_DELETE.toString(),
                id + AVATAR_DELETED_LOG + PROCESS_OWNER + SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return new SuccessResult(AVATAR_DELETED);
    }
}
