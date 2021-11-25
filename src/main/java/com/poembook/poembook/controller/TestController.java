package com.poembook.poembook.api.controller;

import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.repository.PoemRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {
    private PoemRepo poemRepo;

    @GetMapping("/poems")
    List<Poem> getPoems() {

        return poemRepo.findAll();
    }
}
