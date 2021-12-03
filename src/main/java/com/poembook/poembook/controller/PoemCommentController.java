package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.PoemCommentService;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.poem.PoemComment;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@AllArgsConstructor
@PreAuthorize("hasAuthority('poet') or hasAuthority('editor') or hasAuthority('admin') or hasAuthority('superAdmin')")
public class PoemCommentController {
    private final PoemCommentService poemCommentService;


    @PostMapping("/add")
    public Result add(@RequestParam String poemCommentText,
                      @RequestParam Long poemId
    ) {

        return poemCommentService.create(poemCommentText, poemId);
    }

    @PostMapping("/update")
    public Result update(@RequestParam String poemCommentText,
                         @RequestParam Long poemCommentId
    ) {
        return poemCommentService.update(poemCommentId, poemCommentText);
    }

    @DeleteMapping("/delete{poemCommentId}")
    public Result delete(@PathVariable Long poemCommentId) {
        return poemCommentService.delete(poemCommentId);
    }

    @PostMapping("/delete-all")
    public void deletePoemsAllComments(@RequestParam Long poemId) {
        poemCommentService.deletePoemsAllComments(poemId);
    }

    @GetMapping("list/{poemId}")
    public DataResult<List<PoemComment>> listPoemsComments(@PathVariable Long poemId) {
        return poemCommentService.listPoemsComments(poemId);
    }


}
