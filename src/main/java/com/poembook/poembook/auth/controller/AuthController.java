package com.poembook.poembook.auth.controller;

import com.poembook.poembook.auth.constant.Authority;
import com.poembook.poembook.auth.constant.SecurityConstant;
import com.poembook.poembook.auth.domain.UserPrincipal;
import com.poembook.poembook.auth.utility.JWTTokenProvider;
import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.constant.UserConstant;
import com.poembook.poembook.core.exception.entities.UserNotFoundException;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.users.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

import static com.poembook.poembook.auth.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JWTTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String username,
                                      @RequestParam String password

    ) throws UserNotFoundException {
        User loginUser = userService.findUserByUsername(username).getData();
        boolean success = userService.findUserByUsername(username).isSuccess();
        if (!success) {
            throw new UserNotFoundException(UserConstant.USER_NOT_FOUND);
        }
        authenticate(username, password); //hata verirse next line a geçemez
        userService.updateUserLoginDate(loginUser.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }
    @PostMapping("/register")
    public Result register(@RequestBody User user) throws MessagingException {
        return userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
    }
    @PostMapping("/forget-password")
    public Result forgetPassword(@RequestParam String email) throws MessagingException {
        return userService.forgetPassword(email);
    }
    @GetMapping("/forget-password/code/{code}")
    public Result resetPasswordWithCode(@PathVariable String code) throws MessagingException {
        return userService.resetPasswordWithCode(code);
    }

    private HttpHeaders getJwtHeader(UserPrincipal user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) throws BadCredentialsException {
   authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

    }

}
