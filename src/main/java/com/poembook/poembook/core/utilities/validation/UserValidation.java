package com.poembook.poembook.core.utilities.validation;

import com.poembook.poembook.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserValidation {
    private static final String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+.(com|org|net|edu|gov|mil|biz|info|mobi)(.[A-Z]{2})?$";
    private static final String USERNAME_PATTERN = "^\\w{3,20}$";
    private static final String PASSWORD_PATTERN = "^\\w{6,20}$";
    private final UserRepo userRepo;

    public boolean isUsernameExist(String username) {
        return userRepo.findUserByUsername(username) != null;
    }

    public boolean isEmailExist(String email) {
        return userRepo.findUserByEmail(email) != null;
    }

    public boolean isUserIdExist(Long userId) {
        return userRepo.findUserByUserId(userId) != null;
    }

    public boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN, Pattern.CASE_INSENSITIVE);
        //return !pattern.matcher(username).find();
        return true;
    }

    public boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
        // return !pattern.matcher(email).find();
        return true;
    }

    public boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN, Pattern.CASE_INSENSITIVE);
        //return !pattern.matcher(password).find();
        return true;
    }

}
