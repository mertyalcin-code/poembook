package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.LoggerService;
import com.poembook.poembook.business.abstracts.UserService;
import com.poembook.poembook.constant.LoggerConstant;
import com.poembook.poembook.constant.enumaration.Log;
import com.poembook.poembook.constant.enumaration.Role;
import com.poembook.poembook.core.utilities.result.*;
import com.poembook.poembook.core.utilities.service.EmailService;
import com.poembook.poembook.core.utilities.validation.UserValidation;
import com.poembook.poembook.entities.dtos.profile.ProfileFollowers;
import com.poembook.poembook.entities.dtos.profile.ProfileFollowings;
import com.poembook.poembook.entities.dtos.profile.ProfileUser;
import com.poembook.poembook.entities.poem.Poem;
import com.poembook.poembook.entities.poem.PoemComment;
import com.poembook.poembook.entities.poem.PoemLike;
import com.poembook.poembook.entities.users.Avatar;
import com.poembook.poembook.entities.users.Follower;
import com.poembook.poembook.entities.users.PasswordReset;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.poembook.poembook.constant.LoggerConstant.PROCESS_OWNER;
import static com.poembook.poembook.constant.UserConstant.*;

@AllArgsConstructor
@Service
public class UserManager implements UserService {
    private final LoggerService logger;
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidation userRegistrationValidation;
    private final PoemRepo poemRepo;
    private final LikedPoemsRepo likedPoemsRepo;
    private final PoemCommentRepo poemCommentRepo;
    private final FollowersRepo followersRepo;
    private final EmailService emailService;
    private final PasswordResetRepo passwordResetRepo;
    @Override
    public Result addUser(String currentUsername,
                          String firstName,
                          String lastName,
                          String username,
                          String email,
                          String role,
                          boolean isNonLocked,
                          boolean isActive) throws MessagingException {
        if (userRegistrationValidation.isUsernameExist(username)) {
            return new ErrorResult(USERNAME_ALREADY_EXISTS);
        }
        if (userRegistrationValidation.isEmailExist(email)) {
            return new ErrorResult(EMAIL_ALREADY_EXISTS);
        }
        if (!userRegistrationValidation.isUsernameValid(username)) {
            return new ErrorResult(USERNAME_INVALID);
        }
        if (!userRegistrationValidation.isEmailValid(email)) {
            return new ErrorResult(EMAIL_INVALID);
        }
        if (!findUserByUsername(currentUsername).getData().getRole().equals("ROLE_SUPER_ADMIN") && role.equals("ROLE_SUPER_ADMIN")) {
            return new ErrorResult("Süper admin oluşturman için süper admin olman gerekiyor");
        }
        User user = new User();
        String password = generatePassword();
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setPoemCounts(0);
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAvatar(new Avatar(DEFAULT_AVATAR_URL, new Date(), user));
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepo.save(user);
        Follower firstFollower = new Follower();
        firstFollower.setFollowTime(new Date());
        firstFollower.setFrom(user);
        firstFollower.setTo(user);
        followersRepo.save(firstFollower);
        emailService.sendNewPasswordEmail(firstName, password, email);
        logger.log(Log.LOG_USER_ADD.toString(),LoggerConstant.USER_CREATED+user.getUsername()+PROCESS_OWNER+currentUsername);
        return new SuccessResult(USER_CREATED);
    }

    @Override
    public Result register(String firstName,
                           String lastName,
                           String username,
                           String email) throws MessagingException {
        if (userRegistrationValidation.isUsernameExist(username)) {
            return new ErrorResult(USERNAME_ALREADY_EXISTS);
        }
        if (userRegistrationValidation.isEmailExist(email)) {
            return new ErrorResult(EMAIL_ALREADY_EXISTS);
        }
        if (!userRegistrationValidation.isUsernameValid(username)) {
            return new ErrorResult(USERNAME_INVALID);
        }
        if (!userRegistrationValidation.isEmailValid(email)) {
            return new ErrorResult(EMAIL_INVALID);
        }

        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        user.setFirstName(StringUtils.capitalize(firstName));
        user.setLastName(StringUtils.capitalize(lastName));
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setPoemCounts(0);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_POET.name());
        user.setAuthorities(Role.ROLE_POET.getAuthorities());
        user.setAvatar(new Avatar(DEFAULT_AVATAR_URL, new Date(), user));
        userRepo.save(user);
        Follower firstFollower = new Follower();
        firstFollower.setFollowTime(new Date());
        firstFollower.setFrom(user);
        firstFollower.setTo(user);
        followersRepo.save(firstFollower);
        emailService.sendNewPasswordEmail(firstName, password, email);
        logger.log(Log.LOG_USER_REGISTRATION.toString(),LoggerConstant.USER_CREATED+PROCESS_OWNER+username);
        return new SuccessResult(USER_CREATED);
    }

    @Override
    public Result updateUser(String adminUsername,
                             String userUsername,
                             String newFirstName,
                             String newLastName,
                             String newUsername,
                             String newEmail,
                             String role,
                             boolean isNonLocked,
                             boolean isActive) {
        User currentUser = findUserByUsername(userUsername).getData();
        if (userRegistrationValidation.isUsernameExist(newUsername) && !StringUtils.equals(userUsername, newUsername)) {
            return new ErrorResult(USERNAME_ALREADY_EXISTS);
        }
        if (userRegistrationValidation.isEmailExist(newEmail) && !StringUtils.equals(newEmail, currentUser.getEmail())) {
            return new ErrorResult(EMAIL_ALREADY_EXISTS);
        }
        if (!userRegistrationValidation.isUsernameValid(newUsername)) {
            return new ErrorResult(USERNAME_INVALID);
        }
        if (!userRegistrationValidation.isEmailValid(newEmail)) {
            return new ErrorResult(EMAIL_INVALID);
        }
        if (!findUserByUsername(adminUsername).getData().getRole().equals("ROLE_SUPER_ADMIN") && role.equals("ROLE_SUPER_ADMIN")) {
            return new ErrorResult("Süper admin oluşturman için süper admin olman gerekiyor");
        }
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepo.save(currentUser);
        logger.log(Log.LOG_USER_UPDATE.toString(),LoggerConstant.USER_UPDATED+userUsername+PROCESS_OWNER+adminUsername);
        return new SuccessResult(USER_UPDATED);
    }

    @Override
    public Result selfUpdate(String currentUsername, String newFirstname, String newLastname, String facebookAccount, String twitterAccount, String instagramAccount, String aboutMe) {
        User currentUser = findUserByUsername(currentUsername).getData();
        currentUser.setFirstName(newFirstname);
        currentUser.setLastName(newLastname);
        currentUser.setFacebookAccount(facebookAccount);
        currentUser.setTwitterAccount(twitterAccount);
        currentUser.setInstagramAccount(instagramAccount);
        currentUser.setAboutMe(aboutMe);
        userRepo.save(currentUser);
        logger.log(Log.LOG_USER_SELF_UPDATE.toString(),LoggerConstant.USER_UPDATED+currentUsername+PROCESS_OWNER+currentUsername);
        return new SuccessResult(USER_UPDATED);
    }

    @Override
    public Result deleteUser(String username) {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        removeUsersAllPoems(user);
        removeUsersAllFollowInfo(user);
        userRepo.deleteById(user.getUserId());
        logger.log(Log.LOG_USER_DELETE.toString(),LoggerConstant.USER_DELETED+username+PROCESS_OWNER+
                SecurityContextHolder.getContext().getAuthentication().getName());
        return new SuccessResult(USER_DELETED);
    }

    @Override
    public Result resetPassword(String email) throws MessagingException {
        User user = userRepo.findUserByEmail(email);
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepo.save(user);
        emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
        logger.log(Log.LOG_RESET_PASSWORD.toString(),LoggerConstant.RESET_PASSWORD+email+PROCESS_OWNER+
                SecurityContextHolder.getContext().getAuthentication().getName());
        return new SuccessResult(PASSWORD_RESET);
    }

    @Override
    public Result changePassword(String username, String newPassword) {
        User user = findUserByUsername(username).getData();
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        user.setPassword(encodePassword(newPassword));
        userRepo.save(user);
        logger.log(Log.LOG_CHANGE_PASSWORD.toString(),LoggerConstant.CHANGE_PASSWORD+PROCESS_OWNER+
                username);
        return new SuccessResult(PASSWORD_CHANGE);

    }

    @Override
    public DataResult<ProfileUser> getUserProfile(String username) {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        ProfileUser profileUser = new ProfileUser();
        profileUser.setFirstName(user.getFirstName());
        profileUser.setLastName(user.getLastName());
        profileUser.setUsername(user.getUsername());
        profileUser.setJoinDate(user.getJoinDate());
        profileUser.setPoemCount(user.getPoemCounts());
        profileUser.setImageUrl(user.getAvatar().getImageUrl());
        if (user.getTwitterAccount() != null) {
            profileUser.setTwitterAccount(user.getTwitterAccount());
        }
        if (user.getFacebookAccount() != null) {
            profileUser.setFacebookAccount(user.getFacebookAccount());
        }
        if (user.getInstagramAccount() != null) {
            profileUser.setInstagramAccount(user.getInstagramAccount());
        }
        if (user.getAboutMe() != null) {
            profileUser.setAboutMe(user.getAboutMe());
        }
        List<Follower> followings = followersRepo.findAllByFrom(user);
        followings.removeIf(following -> !following.getFrom().isActive());
        List<ProfileFollowings> profileFollowings = new ArrayList<>();
        for (Follower following : followings
        ) {
            profileFollowings.add(new ProfileFollowings(
                    following.getTo().getUsername(),
                    following.getTo().getFirstName(),
                    following.getTo().getLastName(),
                    following.getTo().getAvatar().getImageUrl()
            ));
        }
        profileUser.setFollowings(profileFollowings);
        profileUser.setFollowingCount(profileFollowings.size());

        List<Follower> followers = followersRepo.findAllByTo(user);
        followers.removeIf(follower -> !follower.getTo().isActive());
        List<ProfileFollowers> profileFollowers = new ArrayList<>();
        for (Follower follower : followers
        ) {
            profileFollowers.add(new ProfileFollowers(
                    follower.getFrom().getUsername(),
                    follower.getFrom().getFirstName(),
                    follower.getFrom().getLastName(),
                    follower.getFrom().getAvatar().getImageUrl()
            ));
        }
        profileUser.setFollowers(profileFollowers);
        profileUser.setFollowerCount(profileFollowers.size());
        return new SuccessDataResult<>(profileUser, USER_LISTED);
    }

    @Override
    public DataResult<List<User>> getAllUsers() {
        List<User> users = userRepo.findAll();
        if (users.size() < 1) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        return new SuccessDataResult<>(users, USER_LISTED);
    }

    @Override
    public DataResult<List<User>> getAllPoets() {
        List<User> users = userRepo.findAllByRole("ROLE_POET");
        if (users.size() < 1) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        return new SuccessDataResult<>(users, USER_LISTED);

    }


    @Override
    public DataResult<List<User>> getAllEditors() {
        List<User> users = userRepo.findAllByRole("ROLE_EDITOR");
        if (users.size() < 1) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        return new SuccessDataResult<>(users, USER_LISTED);

    }

    @Override
    public DataResult<List<User>> getAllAdmins() {
        List<User> users = userRepo.findAllByRole("ROLE_ADMIN");
        if (users.size() < 1) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        return new SuccessDataResult<>(users, USER_LISTED);
    }

    @Override
    public DataResult<List<User>> getAllSuperAdmins() {
        List<User> users = userRepo.findAllByRole("ROLE_SUPER_ADMIN");
        if (users.size() < 1) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        }
        return new SuccessDataResult<>(users, USER_LISTED);
    }

    @Override
    public DataResult<User> findUserByUsername(String username) {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        } else {
            return new SuccessDataResult<>(user, USER_LISTED);
        }

    }

    @Override
    public DataResult<User> findUserById(Long id) {
        User user = userRepo.findUserByUserId(id);
        if (user == null) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        } else {
            return new SuccessDataResult<>(user, USER_LISTED);
        }
    }

    @Override
    public DataResult<User> findUserByEmail(String email) {
        User user = userRepo.findUserByEmail(email);
        if (user == null) {
            return new ErrorDataResult<>(USER_NOT_FOUND);
        } else {
            return new SuccessDataResult<>(user, USER_LISTED);
        }

    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);

    }

    private Long generateUserId() {
        return Long.valueOf(RandomStringUtils.randomNumeric(10));
    }

    public void updatePoemCount(User user) {
        user.setPoemCounts(user.getPoems().size());
        userRepo.save(user);
    }

    public void removeUsersAllPoems(User user) {
        List<Poem> poems = user.getPoems();

        for (Poem deletedPoem : poems) {
            List<PoemLike> likedPoems = deletedPoem.getLikedUsers();
            if (likedPoems.size() > 0) {
                likedPoemsRepo.deleteAll(likedPoems);
            }
            List<PoemComment> comments = deletedPoem.getPoemComments();
            if (comments.size() > 0) {
                poemCommentRepo.deleteAll(comments);
            }
        }
        if (poems.size() > 0) {
            poemRepo.deleteAll(poems);
        }
    }

    public void removeUsersAllFollowInfo(User user) {
        List<Follower> followers = followersRepo.findAllByFrom(user);
        List<Follower> followings = followersRepo.findAllByTo(user);
        followersRepo.deleteAll(followers);
        followersRepo.deleteAll(followings);
    }

    @Override
    public void updateUserLoginDate(String username) {
        User user = findUserByUsername(username).getData();
        user.setLastLoginDate(new Date());
        userRepo.save(user);

    }

    @Override
    public Result changeEmail(String currentUsername, String newEmail) {
        User user = findUserByUsername(currentUsername).getData();
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        if (!userRegistrationValidation.isEmailValid(newEmail)) {
            return new ErrorResult(EMAIL_INVALID);
        }
        if (userRegistrationValidation.isEmailExist(newEmail)) {
            return new ErrorResult(EMAIL_ALREADY_EXISTS);
        }
        user.setEmail(newEmail);
        userRepo.save(user);
        logger.log(Log.LOG_CHANGE_EMAIL.toString(),user.getUsername()+LoggerConstant.CHANGE_EMAIL+newEmail+PROCESS_OWNER+
                currentUsername);
        return new SuccessResult(EMAIL_UPDATED);
    }

    @Override
    public Result changeUsername(String currentUsername, String newUsername) {
        User user = findUserByUsername(currentUsername).getData();
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        if (userRegistrationValidation.isUsernameValid(newUsername)) {
            return new ErrorResult(USERNAME_INVALID);
        }
        if (userRegistrationValidation.isUsernameExist(newUsername)) {
            return new ErrorResult(USERNAME_ALREADY_EXISTS);
        }
        user.setUsername(newUsername);
        userRepo.save(user);
        logger.log(Log.LOG_CHANGE_USERNAME.toString(),currentUsername+user.getUsername()+LoggerConstant.CHANGE_USERNAME+newUsername+PROCESS_OWNER+
                currentUsername);
        return new SuccessResult(USERNAME_UPDATED);
    }

    @Override
    public Result makeSuperAdmin(String username) {
        User user = findUserByUsername(username).getData();
        if(user==null){
            return new ErrorResult(USER_NOT_FOUND);
        }
        user.setRole(Role.ROLE_SUPER_ADMIN.name());
        user.setAuthorities(Role.ROLE_SUPER_ADMIN.getAuthorities());
        return new SuccessResult("Artık Süper Admin: "+username);
    }

    @Override
    public Result forgetPassword(String email) throws MessagingException {
      User user=  userRepo.findUserByEmail(email);
        if(user==null){
            return new ErrorResult(USER_NOT_FOUND);
        }
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setEmail(email);
        passwordReset.setCode(RandomStringUtils.randomAlphanumeric(30));
        passwordResetRepo.save(passwordReset);
        String url = "https://poembook-app.herokuapp.com/forget-password/code/"+passwordReset.getCode();
        emailService.sendNewForgetPasswordEmail(user.getFirstName(),url,email);
        return new SuccessResult("Mail adresinize Aktivasyon kodu gönderildi");
    }

    @Override
    public Result resetPasswordWithCode(String code) throws MessagingException {
        PasswordReset passwordReset = passwordResetRepo.findByCode(code);
       if(passwordReset==null){
           return new ErrorResult("Hatalı link");
       }
       resetPassword(passwordReset.getEmail());
       passwordResetRepo.delete(passwordResetRepo.findByCode(code));
       return new SuccessResult("Şifren sıfırlandı ve mail adresine yenisi gönderildi.");
    }

}

