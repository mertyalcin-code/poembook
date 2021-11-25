package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.Result;

public interface PoemLikeService {
    Result like(Long poemId, String username);

    Result unlike(String username, Long poemId);

    void removeAllLikes(Long poemId);

    Result isLiked(String currentUsername, Long poemId);
}

