package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.CategoryService;
import com.poembook.poembook.business.abstracts.FollowerService;
import com.poembook.poembook.business.abstracts.PoemService;
import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.core.utilities.validation.PoemValidation;
import com.poembook.poembook.entities.category.Category;
import com.poembook.poembook.entities.dtos.poemBox.PoemBox;
import com.poembook.poembook.entities.dtos.poemBox.PoemCommentBox;
import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.poem.PoemComment;
import com.poembook.poembook.entities.poem.PoemLike;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.LikedPoemsRepo;
import com.poembook.poembook.repository.PoemCommentRepo;
import com.poembook.poembook.repository.PoemRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.poembook.poembook.constant.CategoryConstant.CATEGORY_NOT_FOUND;
import static com.poembook.poembook.constant.FollowerConstant.NO_FOLLOWING_FOUND;
import static com.poembook.poembook.constant.PoemConstant.*;
import static com.poembook.poembook.constant.UserConstant.USER_NOT_FOUND;

@Service
@AllArgsConstructor
public class PoemManager implements PoemService {
    private PoemRepo poemRepo;
    private UserService userService;
    private CategoryService categoryService;
    private PoemValidation poemValidation;
    private FollowerService followerService;
    private PoemCommentRepo poemCommentRepo;
    private LikedPoemsRepo likedPoemsRepo;

    @Override
    public Result create(String poemTitle, String poemContent, String username, String categoryTitle) {
        Poem poem = new Poem();
        poem.setPoemTitle(poemTitle);
        poem.setPoemContent(poemContent);
        User user = userService.findUserByUsername(username).getData();
        poem.setUser(user);
        Category category = categoryService.findCategoryByCategoryTitle(categoryTitle).getData();
        poem.setCategory(category);
        if (!poemValidation.isPoemTitleValid(poem)) {
            return new ErrorResult(POEM_TITLE_NOT_VALID);
        }
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        if (category == null) {
            return new ErrorResult(CATEGORY_NOT_FOUND);
        }
        if (poemValidation.isPoemExist(poem)) {
            return new ErrorResult(POEM_ALREADY_EXIST);
        }

        if (!poemValidation.isPoemContentValid(poem)) {
            return new ErrorResult(POEM_CONTENT_INVALID);
        }
        poem.setCreationDate(new Date());
        poem.setCommentCount(0);
        poem.setActive(true);
        poem.setLastUpdateDate(new Date());
        poem.setHowManyLikes(0);
        poemRepo.save(poem);
        userService.updatePoemCount(poem.getUser());
        return new SuccessResult(POEM_CREATED);
    }


    @Override
    public Result update(Long poemId, String poemTitle, String poemContent, String currentUsername, String categoryTitle) {
        Poem poem = poemRepo.findByPoemId(poemId);
        if (poem == null) {
            return new ErrorResult(POEM_NOT_FOUND);
        }
        Category category = categoryService.findCategoryByCategoryTitle(categoryTitle).getData();
        poem.setCategory(category);
        if (category == null) {
            return new ErrorResult(CATEGORY_NOT_FOUND);
        }
        if (!poemValidation.isPoemTitleValid(poem)) {
            return new ErrorResult(POEM_TITLE_NOT_VALID);
        }
        if (!poemValidation.isPoemContentValid(poem)) {
            return new ErrorResult(POEM_CONTENT_INVALID);
        }
        poem.setPoemTitle(poemTitle);
        poem.setPoemContent(poemContent);
        poem.setLastUpdateDate(new Date());
        poemRepo.save(poem);
        return new SuccessResult(POEM_UPDATED);
    }

    @Override
    public Result delete(Long id) {

        Poem deletedPoem = poemRepo.findByPoemId(id);
        if (deletedPoem == null) {
            return new ErrorResult(POEM_NOT_FOUND);
        }
        List<PoemLike> likedPoems = deletedPoem.getLikedUsers();
        if (likedPoems.size() > 0) {
            likedPoemsRepo.deleteAll(likedPoems);
        }
        List<PoemComment> comments = deletedPoem.getPoemComments();
        if (comments.size() > 0) {
            poemCommentRepo.deleteAll(comments);
        }

        User user = deletedPoem.getUser();
        poemRepo.delete(deletedPoem);
        userService.updatePoemCount(user);


        return new SuccessResult(POEM_DELETED);
    }


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
    public DataResult<List<PoemBox>> listFollowingsPoemsByDate(String username, int indexStart, int indexEnd) {
        List<String> followings = followerService.getUsersFallowing(username).getData();
        if (followings == null) {
            return new ErrorDataResult<>(NO_FOLLOWING_FOUND);
        }
        List<PoemBox> bigList = new ArrayList<>();
        for (String following : followings) {
            List<Poem> poems = userService.findUserByUsername(following).getData().getPoems();
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
        if (poems == null) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        }
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
        List<Poem> poems = findAllPoem().getData();
        poems.sort(Comparator.comparing(Poem::getHowManyLikes).reversed());
        if (poems.size() > 20) {
            poems.subList(20, poems.size()).clear();
        }
        List<PoemBox> bigList = new ArrayList<>();
        loadInfoToPoemBox(bigList, poems);
        return new SuccessDataResult<>(bigList, POEM_LISTED);
    }

    @Override
    public DataResult<List<PoemBox>> list20MostCommentsPoems() {
        List<Poem> poems = findAllPoem().getData();
        poems.sort(Comparator.comparing(Poem::getCommentCount).reversed());
        if (poems.size() > 20) {
            poems.subList(20, poems.size()).clear();
        }
        List<PoemBox> bigList = new ArrayList<>();
        loadInfoToPoemBox(bigList, poems);
        return new SuccessDataResult<>(bigList, POEM_LISTED);
    }

    @Override
    public DataResult<List<PoemBox>> searchPoems(String search) {
        search = search.toLowerCase();
        List<Poem> poems = findAllPoem().getData();
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
        return new SuccessDataResult<>(bigList, POEM_LISTED);
    }


    @Override
    public DataResult<Poem> findById(Long id) {
        Poem poem = poemRepo.findByPoemId(id);
        if (poem == null) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        }

        return new SuccessDataResult<>(poem, POEM_LISTED);

    }

    @Override
    public DataResult<List<Poem>> findAllByCategoryTitle(String categoryTitle) {
        Category category = categoryService.findCategoryByCategoryTitle(categoryTitle).getData();
        if (category == null) {
            return new ErrorDataResult<>(CATEGORY_NOT_FOUND);
        }
        List<Poem> poems = poemRepo.findAllByCategory(category);
        if (poems.size() < 1) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        } else {
            return new SuccessDataResult<>(poems, POEM_LISTED);
        }
    }

    public void updatePoemCommentCount(Poem poem) {
        poem.setCommentCount(poem.getPoemComments().size());
        poemRepo.save(poem);
    }

    public void updatePoemLikeCount(Poem poem) {
        poem.setHowManyLikes(poem.getLikedUsers().size());
        poemRepo.save(poem);
    }

    @Override
    public DataResult<List<Poem>> findAllPoem() {
        List<Poem> poems = poemRepo.findAll();
        System.out.println(poems);
        if (poems.size() < 1) {
            return new ErrorDataResult<>(POEM_NOT_FOUND);
        }
        return new SuccessDataResult<>(poems, POEM_LISTED);
    }

    @Override
    public Result adminUpdate(Long poemId, String poemTitle, String poemContent, String currentUsername, String categoryTitle, boolean isActive) {
        Poem poem = poemRepo.findByPoemId(poemId);
        if (poem == null) {
            return new ErrorResult(POEM_NOT_FOUND);
        }
        Category category = categoryService.findCategoryByCategoryTitle(categoryTitle).getData();
        poem.setCategory(category);
        if (category == null) {
            return new ErrorResult(CATEGORY_NOT_FOUND);
        }
        if (!poemValidation.isPoemTitleValid(poem)) {
            return new ErrorResult(POEM_TITLE_NOT_VALID);
        }
        if (!poemValidation.isPoemContentValid(poem)) {
            return new ErrorResult(POEM_CONTENT_INVALID);
        }
        poem.setPoemTitle(poemTitle);
        poem.setActive(isActive);
        poem.setPoemContent(poemContent);
        poem.setLastUpdateDate(new Date());
        poemRepo.save(poem);
        return new SuccessResult(POEM_UPDATED);
    }

    @Override
    public DataResult<List<PoemBox>> listByUsernameWithPoembox(String username, int indexStart, int indexEnd) {
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
    public DataResult<PoemBox> getRandomPoem(String currentUsername) {
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
        poems.add(findById(poemId).getData());
        List<PoemBox> bigList = new ArrayList<>();
        loadInfoToPoemBox(bigList, poems);
        return new SuccessDataResult<>(bigList.get(0), POEM_LISTED);
    }

    private void loadInfoToPoemBox(List<PoemBox> bigList, List<Poem> poems) {
        for (Poem poem : poems) {
            PoemBox poemBox = new PoemBox();
            poemBox.setPoemId(poem.getPoemId());
            poemBox.setPoemTitle(poem.getPoemTitle());
            poemBox.setPoemContent(poem.getPoemContent());
            poemBox.setActive(poem.isActive());
            poemBox.setCreationDate(poem.getCreationDate());
            poemBox.setCreationDateInMinute(TimeUnit.MILLISECONDS.toMinutes((new Date()).getTime() - poem.getCreationDate().getTime()));
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
            List<PoemComment> poemComments = poemCommentRepo.findAllByPoem(poem);
            if (poemComments != null) {
                for (PoemComment poemComment : poemComments) {
                    PoemCommentBox poemCommentBox = new PoemCommentBox();
                    poemCommentBox.setPoemCommentId(poemComment.getPoemCommentId());
                    poemCommentBox.setPoemCommentText(poemComment.getPoemCommentText());
                    poemCommentBox.setCommentTimeInMinute(TimeUnit.MILLISECONDS.toMinutes((new Date()).getTime() - poemComment.getCommentTime().getTime()));
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


}
