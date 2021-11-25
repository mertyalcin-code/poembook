package com.poembook.poembook.repository;

import com.poembook.poembook.entities.users.Follower;
import com.poembook.poembook.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowersRepo extends JpaRepository<Follower, Long> {
    List<Follower> findAllByFrom(User user);

    List<Follower> findAllByTo(User user);

    Follower findByFromAndTo(User from, User to);

    Follower findByFollowersId(Long id);
}
