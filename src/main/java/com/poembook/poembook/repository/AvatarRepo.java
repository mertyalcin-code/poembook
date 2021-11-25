package com.poembook.poembook.repository;

import com.poembook.poembook.entities.users.Avatar;
import com.poembook.poembook.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepo extends JpaRepository<Avatar, Long> {

    Avatar findByAvatarId(int avatarId);

    Avatar findByUser(User user);

    void deleteByAvatarId(int avatarId);

}
