package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.dtos.poemBox.PoemBox;
import com.poembook.poembook.entities.poem.Poem;

import java.util.List;

public interface PoemService {
    Result create(String poemTitle, String poemContent, String username, String categoryTitle);

    Result update(Long poemId, String poemTitle, String poemContent, String username, String categoryTitle);

    Result delete(Long id);

    DataResult<List<Poem>> poetsAllPoem(String username);

    DataResult<List<Poem>> poetsAllActivePoem(String username);

    DataResult<List<PoemBox>> listFollowingsPoemsByDate(String username, int indexStart, int indexEnd);

    DataResult<List<PoemBox>> list20MostLikedPoems();

    DataResult<List<PoemBox>> list20MostCommentsPoems();

    DataResult<List<PoemBox>> searchPoems(String search);

    DataResult<Poem> findById(Long id);

    DataResult<List<Poem>> findAllByCategoryTitle(String categoryTitle);

    DataResult<List<PoemBox>> listCategoriesPoemsByDate(String categoryTitle, int indexStart, int indexEnd);

    void updatePoemCommentCount(Poem poem);

    void updatePoemLikeCount(Poem poem);

    DataResult<List<Poem>> findAllPoem();

    Result adminUpdate(Long poemId, String poemTitle, String poemContent, String currentUsername, String categoryTitle, boolean isActive);

    DataResult<List<PoemBox>> listByUsernameWithPoembox(String username, int indexStart, int indexEnd);

    DataResult<PoemBox> getRandomPoem(String currentUsername);

    DataResult<PoemBox> getPoemWithPoemBox(Long poemId);

}
