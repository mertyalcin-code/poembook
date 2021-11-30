package com.poembook.poembook.controller;

import com.poembook.poembook.auth.configuration.UserDetailsServiceImpl;
import com.poembook.poembook.auth.domain.UserPrincipal;
import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.SuccessDataResult;
import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.PoemRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

    @GetMapping("/test")
    DataResult<String> getPoems() {
        return new SuccessDataResult<>(SecurityContextHolder.getContext().getAuthentication().getName()," yeap");
    }
}
