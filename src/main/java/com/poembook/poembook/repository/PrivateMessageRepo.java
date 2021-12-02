package com.poembook.poembook.repository;

import com.poembook.poembook.entities.message.PrivateMessage;
import com.poembook.poembook.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivateMessageRepo extends JpaRepository<PrivateMessage, Long> {
    List<PrivateMessage> findAllByFrom(User from);

    List<PrivateMessage> findAllByTo(User to);

    List<PrivateMessage> findAllByMessageContains(String message);

    List<PrivateMessage> findAllByFromAndTo(User from, User to);
}
