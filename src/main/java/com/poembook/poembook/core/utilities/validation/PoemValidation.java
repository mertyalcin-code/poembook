package com.poembook.poembook.core.utilities.validation;

import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.repository.PoemRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class PoemValidation {
    private static final String POEM_CONTENT_PATTERN = "^\\w{2,5000}$";
    private static final String POEM_TITLE_PATTERN = "^\\w{2,50}$";
    private final PoemRepo poemRepo;

    public boolean isPoemExist(Poem poem) {
        return poemRepo.findByPoemContent(poem.getPoemContent()) != null;
    }

    public boolean isPoemTitleValid(Poem poem) {
        Pattern pattern = Pattern.compile(POEM_TITLE_PATTERN, Pattern.CASE_INSENSITIVE);
        // return pattern.matcher(poem.getPoemTitle()).find();
        return true;

    }

    public boolean isPoemContentValid(Poem poem) {
        Pattern pattern = Pattern.compile(POEM_CONTENT_PATTERN, Pattern.CASE_INSENSITIVE);
        //return pattern.matcher(poem.getPoemContent()).find();
        return true;

    }
}
