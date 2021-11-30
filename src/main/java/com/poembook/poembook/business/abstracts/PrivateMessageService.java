package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.message.PrivateMessage;
import com.poembook.poembook.entities.users.User;

import java.util.List;

public interface PrivateMessageService{
    Result sendMessage(String fromUsername,String toUsername, String message);
    DataResult<List<PrivateMessage>> usersAllMessages(String username);
    DataResult<List<PrivateMessage>> usersAllMessagesWith(String username,String withUsername);
    DataResult<List<User>> usersMessageList(String username);
}
