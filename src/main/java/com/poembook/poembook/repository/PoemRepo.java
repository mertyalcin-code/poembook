package com.poembook.poembook.repository;

import com.poembook.poembook.entities.category.Category;
import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoemRepo extends JpaRepository<Poem, Long> {
    List<Poem> findAllByUser(User user);

    List<Poem> findAllByCategory(Category category);

    Poem findByPoemId(Long id);

    Poem findByPoemContent(String poemContent);

    List<Poem> findAllByIsActive(boolean status);

}
