package com.poembook.poembook.core.utilities.validation;

import com.poembook.poembook.core.utilities.result.ErrorResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.core.utilities.result.SuccessResult;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static com.poembook.poembook.constant.UserConstant.*;

@Service
@AllArgsConstructor
public class UserValidation {
    private static final String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+.(com|org|net|edu|gov|mil|biz|info|mobi)(.[A-Z]{2})?$";
    private static final String USERNAME_PATTERN = "^\\w{3,20}$";
    private static final String FIRST_NAME_PATTERN = "^\\w{3,60}$";
    private static final String LAST_NAME_PATTERN = "^\\w{3,60}$";
    private static final String PASSWORD_PATTERN = "^\\w{6,20}$";
    private static final String FACEBOOK_URL_PATTERN = "((http|https)://)?(www[.])?facebook.com/.+";
    private static final String TWITTER_URL_PATTERN = "((http|https)://)?(www[.])?twitter.com/.+";
    private static final String INSTAGRAM_URL_PATTERN = "((http|https)://)?(www[.])?instagram.com/.+";
    private final UserRepo userRepo;

    public Result validateUserAdd(String currentUsername, String firstName, String lastName, String username, String email, String role) {
        if (isUsernameExist(username)) {
            return new ErrorResult(USERNAME_ALREADY_EXISTS);
        }
        if (isEmailExist(email)) {
            return new ErrorResult(EMAIL_ALREADY_EXISTS);
        }
        if (isUsernameNotValid(username)) {
            return new ErrorResult(USERNAME_INVALID);
        }
        if (isEmailNotValid(email)) {
            return new ErrorResult(EMAIL_INVALID);
        }
        if (isFirstNameNotValid(firstName)) {
            return new ErrorResult(FIRST_NAME_INVALID);
        }
        if (isLastNameNotValid(lastName)) {
            return new ErrorResult(LAST_NAME_INVALID);
        }
        if (!userRepo.findUserByUsername(currentUsername).getRole().equals("ROLE_SUPER_ADMIN") && role.equals("ROLE_SUPER_ADMIN")) {
            return new ErrorResult(SUPER_ADMIN_ADD_ERROR);
        }
        return new SuccessResult();
    }

    public Result validateUserRegistration(String firstName, String lastName, String username, String email) {
        if (isUsernameExist(username)) {
            return new ErrorResult(USERNAME_ALREADY_EXISTS);
        }
        if (isEmailExist(email)) {
            return new ErrorResult(EMAIL_ALREADY_EXISTS);
        }
        if (isUsernameNotValid(username)) {
            return new ErrorResult(USERNAME_INVALID);
        }
        if (isEmailNotValid(email)) {
            return new ErrorResult(EMAIL_INVALID);
        }
        if (isFirstNameNotValid(firstName)) {
            return new ErrorResult(FIRST_NAME_INVALID);
        }
        if (isLastNameNotValid(lastName)) {
            return new ErrorResult(LAST_NAME_INVALID);
        }
        return new SuccessResult();
    }

    public Result validateUpdateUser(String adminUsername, String userUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role) {
        User currentUser = userRepo.findUserByUsername(userUsername);
        if (isUsernameExist(newUsername) && !StringUtils.equals(userUsername, newUsername)) {
            return new ErrorResult(USERNAME_ALREADY_EXISTS);
        }
        if (isEmailExist(newEmail) && !StringUtils.equals(newEmail, currentUser.getEmail())) {
            return new ErrorResult(EMAIL_ALREADY_EXISTS);
        }
        if (isUsernameNotValid(newUsername)) {
            return new ErrorResult(USERNAME_INVALID);
        }
        if (isEmailNotValid(newEmail)) {
            return new ErrorResult(EMAIL_INVALID);
        }
        if (isFirstNameNotValid(newFirstName)) {
            return new ErrorResult(FIRST_NAME_INVALID);
        }
        if (isLastNameNotValid(newLastName)) {
            return new ErrorResult(LAST_NAME_INVALID);
        }
        if (userRepo.findUserByUsername(adminUsername).getRole().equals("ROLE_SUPER_ADMIN") && role.equals("ROLE_SUPER_ADMIN")) {
            return new ErrorResult(SUPER_ADMIN_ADD_ERROR);
        }
        if (userRepo.findUserByUsername(userUsername).getRole().equals("ROLE_SUPER_ADMIN") && !userRepo.findUserByUsername(adminUsername).getRole().equals("ROLE_SUPER_ADMIN")) {
            return new ErrorResult(SUPER_ADMIN_UPDATE_ERROR);
        }
        return new SuccessResult();
    }

    public Result validateSelfUpdate(String currentUsername, String newFirstname, String newLastname,
                                     String facebookAccount, String twitterAccount, String instagramAccount, String aboutMe) {
        if (isFirstNameNotValid(newFirstname)) {
            return new ErrorResult(FIRST_NAME_INVALID);
        }
        if (isLastNameNotValid(newLastname)) {
            return new ErrorResult(LAST_NAME_INVALID);
        }
        if (isFacebookUrlNotValid(facebookAccount)) {
            return new ErrorResult(FACEBOOK_URL_NOT_VALID);
        }
        if (isInstagramUrlNotValid(instagramAccount)) {
            return new ErrorResult(INSTAGRAM_URL_NOT_VALID);
        }
        if (isTwitterUrlNotValid(twitterAccount)) {
            return new ErrorResult(TWITTER_URL_NOT_VALID);
        }
        return new SuccessResult();
    }

    public Result validateChangeEmail(User user, String currentUsername, String newEmail) {
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        if (isEmailNotValid(newEmail)) {
            return new ErrorResult(EMAIL_INVALID);
        }
        if (isEmailExist(newEmail)) {
            return new ErrorResult(EMAIL_ALREADY_EXISTS);
        }
        return new SuccessResult();
    }

    public Result validateChangeUsername(User user, String currentUsername, String newUsername) {
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        if (isUsernameNotValid(newUsername)) {
            return new ErrorResult(USERNAME_INVALID);
        }
        if (isUsernameExist(newUsername)) {
            return new ErrorResult(USERNAME_ALREADY_EXISTS);
        }
        return new SuccessResult();
    }

    public Result validateChangePassword(User user, String newPassword) {
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        if (isPasswordNotValid(newPassword)) {
            return new ErrorResult(PASSWORD_INVALID);
        }
        return new SuccessResult();
    }


    //helpers

    public boolean isUsernameExist(String username) {
        return userRepo.findUserByUsername(username) != null;
    }

    public boolean isEmailExist(String email) {
        return userRepo.findUserByEmail(email) != null;
    }

    private boolean isUserIdExist(Long userId) {
        return userRepo.findUserByUserId(userId) != null;
    }

    public boolean isUsernameNotValid(String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(username).find();

    }

    public boolean isEmailNotValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(email).find();

    }

    public boolean isFirstNameNotValid(String firstName) {
        Pattern pattern = Pattern.compile(FIRST_NAME_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(firstName).find();

    }

    public boolean isLastNameNotValid(String lastName) {
        Pattern pattern = Pattern.compile(LAST_NAME_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(lastName).find();
    }

    public boolean isFacebookUrlNotValid(String facebookUrl) {
        Pattern pattern = Pattern.compile(FACEBOOK_URL_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(facebookUrl).find();
    }

    public boolean isTwitterUrlNotValid(String twitterUrl) {
        Pattern pattern = Pattern.compile(TWITTER_URL_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(twitterUrl).find();
    }

    public boolean isInstagramUrlNotValid(String instagramUrl) {
        Pattern pattern = Pattern.compile(INSTAGRAM_URL_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(instagramUrl).find();
    }

    private boolean isPasswordNotValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN, Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(password).find();

    }


}
