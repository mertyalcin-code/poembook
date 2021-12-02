package com.poembook.poembook.repository;

import com.poembook.poembook.entities.notification.Notice;
import com.poembook.poembook.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepo extends JpaRepository<Notice, Long> {

    List<Notice> findAllByUser(User user);

    Notice findByNoticeId(Long id);
}
