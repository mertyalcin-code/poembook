package com.poembook.poembook.api.controller;

import com.poembook.poembook.business.abstracts.AvatarService;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.users.Avatar;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/avatar")
@AllArgsConstructor
@PreAuthorize("hasAuthority('poet') or hasAuthority('editor') or hasAuthority('admin') or hasAuthority('superAdmin')")
public class AvatarController {
    private AvatarService avatarService;

    @GetMapping("/getall")
    public DataResult<List<Avatar>> getAll() {
        return this.avatarService.getAll();
    }

    @GetMapping("/getbyid/{id}")
    public DataResult<Avatar> getById(@PathVariable int id) {
        return this.avatarService.getById(id);
    }

    @GetMapping("/getbyuserid/{id}")
    public DataResult<Avatar> getByUserId(@PathVariable Long id) {
        return this.avatarService.getByUserId(id);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('category:read')")
    public Result add(@RequestParam MultipartFile avatar, @RequestParam String username) {
        return this.avatarService.add(avatar, username);
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable int id) throws IOException {
        return this.avatarService.delete(id);
    }

}
