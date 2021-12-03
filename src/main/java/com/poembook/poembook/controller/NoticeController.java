package com.poembook.poembook.controller;

import com.poembook.poembook.business.abstracts.NoticeService;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.notification.Notice;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@AllArgsConstructor
@PreAuthorize("hasAuthority('poet') or hasAuthority('editor') or hasAuthority('admin') or hasAuthority('superAdmin')")
public class NoticeController {
    private NoticeService noticeService;

    @DeleteMapping("/delete/{noticeId}")
    public Result delete(@PathVariable Long noticeId

    ) {

        return noticeService.delete(noticeId);
    }

    @PostMapping("/delete-all")
    public Result delete(@RequestParam String username

    ) {
        return noticeService.deleteAll(username);
    }

    @PostMapping("/list-all")
    public DataResult<List<Notice>> listAll(@RequestParam String username
    ) {
        return noticeService.listAll(username);
    }
}
