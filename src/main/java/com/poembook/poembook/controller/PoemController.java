package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.PoemService;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.dtos.poemBox.PoemBox;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/poem")
@AllArgsConstructor
@PreAuthorize("hasAuthority('poet') or hasAuthority('editor') or hasAuthority('admin') or hasAuthority('superAdmin')")
public class PoemController {
    private final PoemService poemService;


    @GetMapping("/list/random")
    public DataResult<PoemBox> getRandomPoem() {
        return poemService.getRandomPoem();
    }

    @GetMapping("/list/search/{search}")
    public DataResult<List<PoemBox>> searchPoems(@PathVariable String search) {
        return poemService.searchPoems(search);
    }

    @GetMapping("/list/with-poembox/{poemId}")
    public DataResult<PoemBox> getPoemWithPoemBox(@PathVariable Long poemId) {
        return poemService.getPoemWithPoemBox(poemId);
    }

    @PostMapping("/list/followings")
    public DataResult<List<PoemBox>> listByFollowings(@RequestParam int indexStart,
                                                      @RequestParam int indexEnd) {
        return poemService.listFollowingsPoemsByDate(indexStart, indexEnd);
    }

    @PostMapping("/list/profile/username")
    public DataResult<List<PoemBox>> listByUsernameWithPoembox( @RequestParam String username,
                                                               @RequestParam int indexStart,
                                                               @RequestParam int indexEnd) {
        return poemService.listByUsernameWithPoembox(username, indexStart, indexEnd);
    }

    @PostMapping("/list/categories")
    public DataResult<List<PoemBox>> listCategoriesPoemsByDate(
                                                               @RequestParam String categoryTitle,
                                                               @RequestParam int indexStart,
                                                               @RequestParam int indexEnd
    ) {
        return poemService.listCategoriesPoemsByDate(categoryTitle, indexStart, indexEnd);
    }

    @GetMapping("/list/populer/most-comment")
    public DataResult<List<PoemBox>> list20MostCommentsPoems() {
        return poemService.list20MostCommentsPoems();
    }

    @GetMapping("/list/populer/most-liked")
    public DataResult<List<PoemBox>> list20MostLikedPoems() {
        return poemService.list20MostLikedPoems();
    }

    @PostMapping("/create")
    public Result create(@RequestParam String poemTitle,
                         @RequestParam String poemContent,
                         @RequestParam String categoryTitle
    ) {
        System.out.println(poemTitle);
        return poemService.create(poemTitle, poemContent, categoryTitle);
    }

    @PostMapping("/update")
    public Result update(@RequestParam Long poemId,
                         @RequestParam String poemTitle,
                         @RequestParam String poemContent,
                         @RequestParam String categoryTitle

    ) {
        return poemService.update(poemId, poemTitle, poemContent, categoryTitle);
    }


    @DeleteMapping("/delete/{poemId}")
    public Result delete(@PathVariable Long poemId) {
        return poemService.delete(poemId);
    }


}
