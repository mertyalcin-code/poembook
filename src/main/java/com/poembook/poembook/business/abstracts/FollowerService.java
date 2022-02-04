package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;

import java.util.List;

public interface FollowerService {
    Result create(String from, String to);

    Result isFollowing(String from, String to);

    Result delete(String fromUsername, String toUsername);

    DataResult<List<String>> getUsersFollowers(String username);

    DataResult<List<String>> getUsersFollowing(String username);


}
