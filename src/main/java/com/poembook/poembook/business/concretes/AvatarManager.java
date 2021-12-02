package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.AvatarService;
import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.core.utilities.service.cloudinary.CloudService;
import com.poembook.poembook.entities.users.Avatar;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.AvatarRepo;
import com.poembook.poembook.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AvatarManager implements AvatarService {
    private AvatarRepo avatarRepo;
    private CloudService cloudService;
    private UserService userService;
    private UserRepo userRepo;

    @Override
    public DataResult<List<Avatar>> getAll() {
        return new SuccessDataResult<>(this.avatarRepo.findAll(), "listelendi");
    }

    @Override
    public DataResult<Avatar> getById(int id) {
        return new SuccessDataResult<>(this.avatarRepo.findByAvatarId(id), "resim:");
    }

    @Override
    public DataResult<Avatar> getByUserId(Long id) {
        return new SuccessDataResult<>(this.avatarRepo.findByUser(userService.findUserById(id).getData()), "getirdim");
    }

    @Override
    public Result add(MultipartFile multipartFile, String username) {

        DataResult<Map<String, String>> result = this.cloudService.upload(multipartFile);
        if (!result.isSuccess()) {
            return new ErrorResult(result.getMessage());
        }
        User user = this.userService.findUserByUsername(username).getData();
        System.out.println(user.getUsername());
        Avatar avatar = new Avatar();
        avatar.setImageUrl(result.getData().get("url"));
        avatar.setPublic_id(result.getData().get("public_id"));
        avatar.setUploadedDate(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));
        user.setAvatar(avatar);
        userRepo.save(user);

        return new SuccessResult("photo added");


    }

    @Override
    public Result delete(int id) throws IOException {
        String public_id = this.avatarRepo.findByAvatarId(id).getPublic_id();
        this.cloudService.delete(public_id);
        this.avatarRepo.deleteByAvatarId(id);
        return new SuccessResult("deleted");
    }
}
