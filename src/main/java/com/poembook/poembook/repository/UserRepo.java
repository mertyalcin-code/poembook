package com.poembook.poembook.repository;

import com.poembook.poembook.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User findUserByUserId(Long id);

    List<User> findAllByRole(String role);
    // List<User> findAllByIsActive(boolean status);
    //List<User> findAllByNotLocked(boolean status);


}
