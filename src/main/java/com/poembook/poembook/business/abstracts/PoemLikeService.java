package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.Result;

public interface PoemLikeService {
    Result like(Long poemId);

    Result unlike(Long poemId);

    void removeAllLikes(Long poemId);

    Result isLiked(Long poemId);
}

