package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.PoemLikeService;
import com.poembook.poembook.business.abstracts.PoemService;
import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.core.utilities.result.ErrorResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.core.utilities.result.SuccessResult;
import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.poem.PoemLike;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.LikedPoemsRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.poembook.poembook.constant.LikedPoemConstant.*;

@Service
@AllArgsConstructor
public class PoemLikeManager implements PoemLikeService {
    private final LikedPoemsRepo likedPoemsRepo;
    private final PoemService poemService;
    private final UserService userService;

    @Override
    public Result like(Long poemId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(username).getData();
        Poem poem = poemService.findById(poemId).getData();
        if (likedPoemsRepo.findByUserAndPoem(user, poem) != null) {
            return new ErrorResult(POEM_ALREADY_LIKED);
        }
        PoemLike likedPoem = new PoemLike();
        likedPoem.setLikedAt(LocalDateTime.now().atZone(ZoneId.of("UTC")));
        likedPoem.setUser(user);
        likedPoem.setPoem(poem);
        likedPoemsRepo.save(likedPoem);
        poemService.updatePoemLikeCount(likedPoem.getPoem());
        return new SuccessResult(POEM_LIKED);
    }


    @Override
    public Result unlike( Long poemId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(username).getData();
        Poem poem = poemService.findById(poemId).getData();
        PoemLike poemLike = likedPoemsRepo.findByUserAndPoem(user, poem);
        if (poemLike == null) {
            return new ErrorResult(POEM_ALREADY_UNLIKED);
        }
        likedPoemsRepo.delete(poemLike);
        poemService.updatePoemLikeCount(poem);
        return new SuccessResult(POEM_UNLIKED);
    }

    @Override
    public void removeAllLikes(Long poemId) {
        Poem poem = poemService.findById(poemId).getData();
        List<PoemLike> likedPoems = poem.getLikedUsers();
        if (likedPoems.size() > 0) {
            likedPoemsRepo.deleteAll(likedPoems);
        }

    }

    @Override
    public Result isLiked(Long poemId) {
        return null; //gerekirse
    }


}
