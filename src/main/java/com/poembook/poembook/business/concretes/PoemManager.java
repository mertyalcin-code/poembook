package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.*;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.core.utilities.validation.PoemValidation;
import com.poembook.poembook.entities.category.Category;
import com.poembook.poembook.entities.dtos.poemBox.PoemBox;
import com.poembook.poembook.entities.dtos.poemBox.PoemCommentBox;
import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.poem.PoemComment;
import com.poembook.poembook.entities.poem.PoemLike;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.CategoryRepo;
import com.poembook.poembook.repository.LikedPoemsRepo;
import com.poembook.poembook.repository.PoemCommentRepo;
import com.poembook.poembook.repository.PoemRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.poembook.poembook.constant.CategoryConstant.CATEGORY_NOT_FOUND;
import static com.poembook.poembook.constant.FollowerConstant.NO_FOLLOWING_FOUND;
import static com.poembook.poembook.constant.LoggerConstant.*;
import static com.poembook.poembook.constant.PoemConstant.*;
import static com.poembook.poembook.constant.UserConstant.USER_NOT_FOUND;
import static com.poembook.poembook.constant.enumaration.Log.*;

@Service
@AllArgsConstructor
public class PoemManager implements PoemService {
    private PoemRepo poemRepo;
    private UserService userService;
    private CategoryService categoryService;
    private CategoryRepo categoryRepo;
    private PoemValidation validation;
    private FollowerService followerService;
    private PoemCommentRepo poemCommentRepo;
    private LikedPoemsRepo likedPoemsRepo;
    private LoggerService logger;


    //user Methods
    @Override
    public Result create(String poemTitle, String poemContent, String categoryTitle) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(username).getData();
        Category category = categoryRepo.findByCategoryTitle(categoryTitle);
        if (!validation.validatePoemCreate(user, poemTitle, poemContent, username, categoryTitle).isSuccess()) {
            return new ErrorResult(validation.validatePoemCreate(user, poemTitle, poemContent, username, categoryTitle).getMessage());
        }
        Poem poem = new Poem();
        poem.setPoemTitle(poemTitle);
        poem.setPoemContent(poemContent);
        poem.setUser(user);
        poem.setCategory(category);
        poem.setCreationDate(LocalDateTime.now().atZone(ZoneId.of("UTC")));
        poem.setCommentCount(0);
        poem.setActive(true);
        poem.setLastUpdateDate(LocalDateTime.now().atZone(ZoneId.of("UTC")));
        poem.setHowManyLikes(0);
        poemRepo.save(poem);
        userService.updatePoemCount(poem.getUser());

        logger.log(LOG_POEM_CREATE.toString(),
                username + POEM_CREATED_LOG + poemTitle + PROCESS_OWNER + SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return new SuccessResult(POEM_CREATED);

    }


    @Override
    public Result update(Long poemId, String poemTitle, String poemContent, String categoryTitle) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Poem poem = poemRepo.findByPoemId(poemId);
        Category category = categoryRepo.findByCategoryTitle(categoryTitle);
        if (!validation.validatePoemUpdate(poem, poemTitle, poemContent, currentUsername, categoryTitle).isSuccess()) {
            return new ErrorResult(validation.validatePoemUpdate(poem, poemTitle, poemContent, currentUsername, categoryTitle).getMessage());
        }
        poem.setCategory(category);
        poem.setPoemTitle(poemTitle);
        poem.setPoemContent(poemContent);
        poem.setLastUpdateDate(LocalDateTime.now().atZone(ZoneId.of("UTC")));
        poemRepo.save(poem);
        logger.log(LOG_POEM_UPDATE.toString(),
                currentUsername + POEM_UPDATED_LOG + poemTitle + PROCESS_OWNER + SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return new SuccessResult(POEM_UPDATED);
    }

    @Override
    public Result delete(Long id) {
        Poem deletedPoem = poemRepo.findByPoemId(id);
        if (deletedPoem == null) {
            return new ErrorResult(POEM_NOT_FOUND);
        }
        deletePoemData(deletedPoem);
        User user = deletedPoem.getUser();
        poemRepo.delete(deletedPoem);
        userService.updatePoemCount(user);
        logger.log(LOG_POEM_DELETE.toString(),
                deletedPoem.getUser().getUsername() + POEM_DELETED_LOG + deletedPoem.getPoemTitle() + PROCESS_OWNER + SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return new SuccessResult(POEM_DELETED);
    }


    @Override
    public DataResult<List<PoemBox>> listFollowingsPoemsByDate(int indexStart, int indexEnd) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<String> followings = followerService.getUsersFollowing(username).getData();
        if (followings == null) {
            return new ErrorDataResult<>(NO_FOLLOWING_FOUND);
        }
        List<PoemBox> bigList = new ArrayList<>();
        for (String following : followings) {
            List<Poem> poems = userService.findUserByUsername(following).getData().getPoems();
            poems.removeIf(poem -> !poem.isActive());
            loadInfoToPoemBox(bigList, poems);
        }
        bigList.sort(Comparator.comparing(PoemBox::getCreationDate).reversed());
        
        if (indexStart > 0) {
            bigList.subList(0, indexStart).clear();
        }
        if (indexEnd > bigList.size()) {
            indexEnd = bigList.size();
        }
        bigList.subList(indexEnd, bigList.size()).clear();
        return new SuccessDataResult<>(bigList, POEM_LISTED);
    }

    @Override
    public DataResult<List<PoemBox>> listCategoriesPoemsByDate(String categoryTitle, int indexStart, int indexEnd) {
        List<Poem> poems = findAllByCategoryTitle(categoryTitle).getData();

        if (!findAllByCategoryTitle(categoryTitle).isSuccess()) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        }
        poems.removeIf(poem -> !poem.isActive());
        List<PoemBox> bigList = new ArrayList<>();
        loadInfoToPoemBox(bigList, poems);
        bigList.sort(Comparator.comparing(PoemBox::getCreationDate).reversed());

        if (indexEnd > bigList.size()) {
            indexEnd = bigList.size();
        }
        return new SuccessDataResult<>(bigList.subList(indexStart, indexEnd), POEM_LISTED);
    }


    @Override
    public DataResult<List<PoemBox>> list20MostLikedPoems() {
        List<PoemBox> bigList = new ArrayList<>();
        loadInfoToPoemBox(bigList, poemRepo.findTop20ByOrderByHowManyLikesDesc());
        return new SuccessDataResult<>(bigList, POEM_LISTED);
    }

    @Override
    public DataResult<List<PoemBox>> list20MostCommentsPoems() {
        List<PoemBox> bigList = new ArrayList<>();
        loadInfoToPoemBox(bigList, poemRepo.findTop20ByOrderByCommentCountDesc());
        return new SuccessDataResult<>(bigList, POEM_LISTED);
    }

    @Override
    public DataResult<List<PoemBox>> searchPoems(String search) {
        search = search.toLowerCase();
        List<Poem> poems = findAllPoem().getData();
        poems.removeIf(poem -> !poem.isActive());
        poems.sort(Comparator.comparing(Poem::getCreationDate).reversed());
        List<Poem> newPoems = new ArrayList<>();
        for (Poem poem : poems) {
            if (poem.getPoemTitle().toLowerCase().contains(search)) {
                newPoems.add(poem);
            }
            if (!poem.getPoemTitle().toLowerCase().contains(search) && poem.getPoemContent().toLowerCase().contains(search)) {
                newPoems.add(poem);
            }
        }
        List<PoemBox> bigList = new ArrayList<>();
        loadInfoToPoemBox(bigList, newPoems);

        logger.log(LOG_POEM_SEARCH.toString(),
                SecurityContextHolder.getContext().getAuthentication().getName() + POEM_SEARCH_LOG + search
        );
        return new SuccessDataResult<>(bigList, POEM_LISTED);
    }


    @Override
    public DataResult<List<Poem>> findAllByCategoryTitle(String categoryTitle) {
        Category category = categoryService.findCategoryByCategoryTitle(categoryTitle).getData();
        if (category == null) {
            return new ErrorDataResult<>(CATEGORY_NOT_FOUND);
        }
        List<Poem> poems = poemRepo.findAllByCategory(category);
        poems.removeIf(poem -> !poem.isActive());
        if (poems.size() < 1) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        } else {
            return new SuccessDataResult<>(poems, POEM_LISTED);
        }
    }

    @Override
    public DataResult<List<PoemBox>> listByUsernameWithPoembox(String username,int indexStart, int indexEnd) {
        User user = userService.findUserByUsername(username).getData();
        if (user == null) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        List<Poem> poems = poemRepo.findAllByUser(user);
        if (poems == null) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        }
        List<PoemBox> bigList = new ArrayList<>();
        loadInfoToPoemBox(bigList, poems);
        bigList.sort(Comparator.comparing(PoemBox::getCreationDate).reversed());
        if (indexStart > 0) {
            bigList.subList(0, indexStart).clear();
        }
        if (indexEnd > bigList.size()) {
            indexEnd = bigList.size();
        }
        bigList.subList(indexEnd, bigList.size()).clear();
        return new SuccessDataResult<>(bigList, POEM_LISTED);
    }

    @Override
    public DataResult<PoemBox> getRandomPoem() {
       String currentUsername= SecurityContextHolder.getContext().getAuthentication().getName();
        List<Poem> poems = findAllPoem().getData();
        poems.removeIf(poem -> Objects.equals(poem.getUser().getUsername(), currentUsername));
        List<PoemBox> bigList = new ArrayList<>();
        loadInfoToPoemBox(bigList, poems);
        int index = (int) (Math.random() * bigList.size());
        return new SuccessDataResult<>(bigList.get(index), POEM_LISTED);
    }

    @Override
    public DataResult<PoemBox> getPoemWithPoemBox(Long poemId) {
        List<Poem> poems = new ArrayList<>();
        Poem poem = findById(poemId).getData();
        if (poem == null) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        }
        poems.add(poem);
        List<PoemBox> bigList = new ArrayList<>();
        loadInfoToPoemBox(bigList, poems);
        return new SuccessDataResult<>(bigList.get(0), POEM_LISTED);
    }

    //Editor Methods
    @Override
    public DataResult<List<Poem>> poetsAllPoem(String username) {
        User user = userService.findUserByUsername(username).getData();
        if (user == null) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        List<Poem> poems = user.getPoems();
        if (poems.size() < 1) {
            return new ErrorDataResult<>(NO_POEM_FOUND);
        } else {
            return new SuccessDataResult<>(poems, POEM_LISTED);
        }
    }

    @Override
    public DataResult<List<Poem>> poetsAllActivePoem(String username) {
        User user = userService.findUserByUsername(username).getData();
        if (user == null) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        List<Poem> poems = user.getPoems();
        poems.removeIf(poem -> !poem.isActive());
        if (poems.size() < 1) {
            return new ErrorDataResult<>(NO_POEM_FOUND);

        } else {
            return new SuccessDataResult<>(poems, POEM_LISTED);
        }
    }

    @Override
    public Result adminUpdate(Long poemId, String poemTitle, String poemContent, String categoryTitle, boolean isActive) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Poem poem = poemRepo.findByPoemId(poemId);
        Category category = categoryService.findCategoryByCategoryTitle(categoryTitle).getData();
        if (!validation.validateAdminUpdate(poem, poemTitle, poemContent, currentUsername, categoryTitle).isSuccess()) {
            return new ErrorResult(validation.validateAdminUpdate(poem, poemTitle, poemContent, currentUsername, categoryTitle).getMessage());
        }
        poem.setCategory(category);
        poem.setPoemTitle(poemTitle);
        poem.setActive(isActive);
        poem.setPoemContent(poemContent);
        poem.setLastUpdateDate(LocalDateTime.now().atZone(ZoneId.of("UTC")));
        poemRepo.save(poem);
        logger.log(LOG_POEM_UPDATE.toString(),
                currentUsername + POEM_UPDATED_LOG + poemTitle + PROCESS_OWNER + SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return new SuccessResult(POEM_UPDATED);
    }

    @Override
    public DataResult<List<Poem>> findAllPoem() {
        List<Poem> poems = poemRepo.findAll();
        if (poems.size() < 1) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        }
        return new SuccessDataResult<>(poems, POEM_LISTED);
    }

    //helpers
    private void loadInfoToPoemBox(List<PoemBox> bigList, List<Poem> poems) {
        for (Poem poem : poems) {
            PoemBox poemBox = new PoemBox();
            poemBox.setPoemId(poem.getPoemId());
            poemBox.setPoemTitle(poem.getPoemTitle());
            poemBox.setPoemContent(poem.getPoemContent());
            poemBox.setActive(poem.isActive());
            poemBox.setCreationDate(poem.getCreationDate());
            poemBox.setCreationDateInMinute(Duration.between(poem.getCreationDate(),LocalDateTime.now().atZone(ZoneId.of("UTC"))).toMinutes());
            poemBox.setLastUpdateDate(poem.getLastUpdateDate());
            poemBox.setCommentCount(poem.getCommentCount());
            poemBox.setHowManyLikes(poem.getHowManyLikes());
            poemBox.setCategoryTitle(poem.getCategory().getCategoryTitle());
            poemBox.setUsername(poem.getUser().getUsername());
            poemBox.setFirstName(poem.getUser().getFirstName());
            poemBox.setLastName(poem.getUser().getLastName());
            poemBox.setAvatar(poem.getUser().getAvatar().getImageUrl());
            poemBox.setWhoLiked(whoLiked(poem.getPoemId()));
            List<PoemCommentBox> poemCommentBoxes = new ArrayList<>();
            List<PoemComment> poemComments = poem.getPoemComments();
            if (poemComments != null) {
                for (PoemComment poemComment : poemComments) {
                    PoemCommentBox poemCommentBox = new PoemCommentBox();
                    poemCommentBox.setPoemCommentId(poemComment.getPoemCommentId());
                    poemCommentBox.setPoemCommentText(poemComment.getPoemCommentText());
                    poemCommentBox.setCommentTimeInMinute(Duration.between(poemComment.getCommentTime(),LocalDateTime.now().atZone(ZoneId.of("UTC"))).toMinutes());
                    poemCommentBox.setLastCommentUpdateTime(poemComment.getLastCommentUpdateTime());
                    poemCommentBox.setUsername(poemComment.getUser().getUsername());
                    poemCommentBox.setUserAvatar(poemComment.getUser().getAvatar().getImageUrl());
                    poemCommentBox.setUserFirstName(poemComment.getUser().getFirstName());
                    poemCommentBox.setUserLastName(poemComment.getUser().getLastName());
                    poemCommentBoxes.add(poemCommentBox);
                }
                poemBox.setComments(poemCommentBoxes);
            }
            bigList.add(poemBox);
        }

    }


    public List<String> whoLiked(Long poemId) {
        List<PoemLike> likedPoems = poemRepo.findByPoemId(poemId).getLikedUsers();
        List<String> usernames = new ArrayList<>();
        for (PoemLike likedPoem : likedPoems
        ) {
            usernames.add(likedPoem.getUser().getUsername());
        }
        return usernames;
    }


    private void deletePoemData(Poem deletedPoem) {
        List<PoemLike> likedPoems = deletedPoem.getLikedUsers();
        if (likedPoems.size() > 0) {
            likedPoemsRepo.deleteAll(likedPoems);
        }
        List<PoemComment> comments = deletedPoem.getPoemComments();
        if (comments.size() > 0) {
            poemCommentRepo.deleteAll(comments);
        }
    }



    @Override
    public DataResult<Poem> findById(Long id) {
        Poem poem = poemRepo.findByPoemId(id);
        if (poem == null) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        }

        return new SuccessDataResult<>(poem, POEM_LISTED);

    }

    public void updatePoemCommentCount(Poem poem) {
        poem.setCommentCount(poem.getPoemComments().size());
        poemRepo.save(poem);
    }

    public void updatePoemLikeCount(Poem poem) {
        poem.setHowManyLikes(poem.getLikedUsers().size());
        poemRepo.save(poem);
    }
}
