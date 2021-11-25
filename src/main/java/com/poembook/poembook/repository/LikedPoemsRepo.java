package com.poembook.poembook.repository;

import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.poem.PoemLike;
import com.poembook.poembook.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedPoemsRepo extends JpaRepository<PoemLike, Long> {
    PoemLike findByUserAndPoem(User user, Poem poem);

    PoemLike findByLikedPoemId(Long id);

}
