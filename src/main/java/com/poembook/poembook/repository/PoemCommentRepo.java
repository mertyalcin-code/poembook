package com.poembook.poembook.repository;

import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.poem.PoemComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PoemCommentRepo extends JpaRepository<PoemComment, Long> {
    List<PoemComment> findAllByPoem(Poem poem);

    PoemComment findByPoemCommentId(Long id);

}
