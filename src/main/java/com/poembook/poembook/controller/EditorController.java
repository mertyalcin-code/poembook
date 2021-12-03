package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.CategoryService;
import com.poembook.poembook.business.abstracts.PoemService;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.repository.PoemRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/editor")
@AllArgsConstructor
@PreAuthorize("hasAuthority('editor') or hasAuthority('admin') or hasAuthority('superAdmin')")
public class EditorController {
    private CategoryService categoryService;
    private PoemService poemService;

    @PostMapping("/category/add")
    public Result createCategory(@RequestParam String categoryTitle,
                                 @RequestParam boolean isActive) {
        return categoryService.create(categoryTitle, isActive);
    }

    @PostMapping("/category/update")
    public Result updateCategory(@RequestParam int categoryId,
                                 @RequestParam String newCategoryTitle,
                                 @RequestParam boolean isActive) {

        return categoryService.update(categoryId, newCategoryTitle, isActive);
    }

    @PostMapping("/category/delete")
    public Result deleteCategory(@RequestParam String categoryTitle
    ) {
        return categoryService.delete(categoryTitle);
    }

    @GetMapping("/poem/list")
    public DataResult<List<Poem>> findAllPoem() {
        return poemService.findAllPoem();
    }

    @PostMapping("/poem/update")
    public Result adminUpdate(@RequestParam Long poemId,
                              @RequestParam String poemTitle,
                              @RequestParam String poemContent,
                              @RequestParam String categoryTitle,
                              @RequestParam boolean isActive

    ) {
        return poemService.adminUpdate(poemId, poemTitle, poemContent, categoryTitle, isActive);
    }

}
