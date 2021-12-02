package com.poembook.poembook.repository;

import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.poem.PoemLike;
import com.poembook.poembook.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikedPoemsRepo extends JpaRepository<PoemLike, Long> {
    PoemLike findByUserAndPoem(User user, Poem poem);
    List<PoemLike> findByUser(User user);
    PoemLike findByLikedPoemId(Long id);

}
