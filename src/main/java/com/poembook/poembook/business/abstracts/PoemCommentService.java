package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.poem.PoemComment;

import java.util.List;

public interface PoemCommentService {

    Result read(Long poemCommentId);

    Result create(String poemCommentText, Long poemId);

    Result update(Long poemCommentId, String poemCommentText);

    Result delete(Long poemCommentId);

    void deletePoemsAllComments(Long poemId);

    DataResult<List<PoemComment>> listPoemsComments(Long poemId);


}
