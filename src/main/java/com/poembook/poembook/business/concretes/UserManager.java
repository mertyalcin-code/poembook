package com.poembook.poembook.business.concretes;

import com.poembook.poembook.business.abstracts.FollowerService;
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
import com.poembook.poembook.entities.users.Avatar;
import com.poembook.poembook.entities.users.Follower;
import com.poembook.poembook.entities.users.PasswordReset;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.poembook.poembook.constant.EmailConstant.EMAIL_SEND_SUCCESS;
import static com.poembook.poembook.constant.LoggerConstant.FORGET_PASSWORD_LOG;
import static com.poembook.poembook.constant.LoggerConstant.PROCESS_OWNER;
import static com.poembook.poembook.constant.UserConstant.*;
import static com.poembook.poembook.constant.enumaration.Log.LOG_FORGET_PASSWORD;

@AllArgsConstructor
@Service
public class UserManager implements UserService {
    private final LoggerService logger;
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserValidation validation;
    private final PoemRepo poemRepo;
    private final LikedPoemsRepo likedPoemsRepo;
    private final PoemCommentRepo poemCommentRepo;
    private final FollowersRepo followersRepo;
    private final EmailService emailService;
    private final PasswordResetRepo passwordResetRepo;
    private final NoticeRepo noticeRepo;
    private final PrivateMessageRepo privateMessageRepo;
    private final FollowerService followerService;

    //Auth methods
    @Override
    public Result register(String firstName,
                           String lastName,
                           String username,
                           String email) throws MessagingException {

        if (!validation.validateUserRegistration(firstName, lastName, username, email).isSuccess()) {
            return new ErrorResult(validation.validateUserRegistration(firstName, lastName, username, email).getMessage());
        }
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        user.setFirstName(StringUtils.capitalize(firstName));
        user.setLastName(StringUtils.capitalize(lastName));
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));
        user.setPassword(encodePassword(password));
        user.setPoemCounts(0);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_POET.name());
        user.setAuthorities(Role.ROLE_POET.getAuthorities());
        user.setAvatar(new Avatar(DEFAULT_AVATAR_URL, LocalDateTime.now().atZone(ZoneId.of("UTC+3")), user));
        makeFirstFollower(firstName, email, user, password);
        logger.log(Log.LOG_USER_REGISTRATION.toString(), LoggerConstant.USER_CREATED_LOG + PROCESS_OWNER + username);
        return new SuccessResult(USER_CREATED);
    }

    @Override
    public Result forgetPassword(String email) throws MessagingException {
        User user = userRepo.findUserByEmail(email);
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setEmail(email);
        passwordReset.setCreationDate(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));
        passwordReset.setCode(RandomStringUtils.randomAlphanumeric(30));
        passwordResetRepo.save(passwordReset);
        String url = WEB_SITE_URL + "/forget-password/code/" + passwordReset.getCode();
        emailService.sendNewForgetPasswordEmail(user.getFirstName(), url, email);
        logger.log(LOG_FORGET_PASSWORD.toString(),
                FORGET_PASSWORD_LOG + email
        );
        return new SuccessResult(EMAIL_SEND_SUCCESS);
    }

    @Override
    public Result resetPasswordWithCode(String code) throws MessagingException {
        PasswordReset passwordReset = passwordResetRepo.findByCode(code);
        if (passwordReset == null) {
            return new ErrorResult(PASSWORD_RESET_LINK_BROKEN);
        }
        resetPassword(passwordReset.getEmail());
        passwordResetRepo.delete(passwordResetRepo.findByCode(code));
        return new SuccessResult(PASSWORD_RESET_MESSAGE);
    }

    // Admin Methods
    @Override
    public Result addUser(String currentUsername,
                          String firstName,
                          String lastName,
                          String username,
                          String email,
                          String role,
                          boolean isNonLocked,
                          boolean isActive) throws MessagingException {
        if (!validation.validateUserAdd(currentUsername, firstName, lastName, username, email, role).isSuccess()) {
            return new ErrorResult(validation.validateUserAdd(currentUsername, firstName, lastName, username, email, role).getMessage());
        }
        User user = new User();
        String password = generatePassword();
        user.setUserId(generateUserId());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setJoinDate(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setPoemCounts(0);
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setAvatar(new Avatar(DEFAULT_AVATAR_URL, LocalDateTime.now().atZone(ZoneId.of("UTC+3")), user));
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        makeFirstFollower(firstName, email, user, password);
        logger.log(Log.LOG_USER_ADD.toString(), LoggerConstant.USER_CREATED_LOG + user.getUsername() + PROCESS_OWNER + currentUsername);
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
        if (!validation.validateUpdateUser(adminUsername, userUsername, newFirstName, newLastName, newUsername, newEmail, role).isSuccess()) {
            return new ErrorResult(validation.validateUpdateUser(adminUsername, userUsername, newFirstName, newLastName, newUsername, newEmail, role).getMessage());
        }
        User currentUser = userRepo.findUserByUsername(userUsername);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepo.save(currentUser);
        logger.log(Log.LOG_USER_UPDATE.toString(), LoggerConstant.USER_UPDATED_LOG + userUsername + PROCESS_OWNER + adminUsername);
        return new SuccessResult(USER_UPDATED);
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
        logger.log(Log.LOG_RESET_PASSWORD.toString(), LoggerConstant.RESET_PASSWORD_LOG + email + PROCESS_OWNER +
                SecurityContextHolder.getContext().getAuthentication().getName());
        return new SuccessResult(PASSWORD_RESET);
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

    //Super Admin Methods

    @Override
    public Result deleteUser(String username) {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        deleteUsersData(user);
        userRepo.deleteById(user.getUserId());
        logger.log(Log.LOG_USER_DELETE.toString(), LoggerConstant.USER_DELETED_LOG + username + PROCESS_OWNER +
                SecurityContextHolder.getContext().getAuthentication().getName());
        return new SuccessResult(USER_DELETED);
    }

    @Override
    public Result makeSuperAdmin(String username) {
        User user = findUserByUsername(username).getData();
        if (user == null) {
            return new ErrorResult(USER_NOT_FOUND);
        }
        user.setRole(Role.ROLE_SUPER_ADMIN.name());
        user.setAuthorities(Role.ROLE_SUPER_ADMIN.getAuthorities());
        return new SuccessResult(MAKE_SUPER_ADMIN_SUCCESS + username);
    }

    //User Methods
    @Override
    public Result selfUpdate(String currentUsername, String newFirstname, String newLastname, String facebookAccount, String twitterAccount, String instagramAccount, String aboutMe) {
        if (!validation.validateSelfUpdate(currentUsername, newFirstname, newLastname, facebookAccount, twitterAccount, instagramAccount, aboutMe).isSuccess()) {
            return new ErrorResult(validation.validateUpdateUser(currentUsername, newFirstname, newLastname, facebookAccount, twitterAccount, instagramAccount, aboutMe).getMessage());
        }
        User currentUser = findUserByUsername(currentUsername).getData();
        currentUser.setFirstName(newFirstname);
        currentUser.setLastName(newLastname);
        currentUser.setFacebookAccount(facebookAccount);
        currentUser.setTwitterAccount(twitterAccount);
        currentUser.setInstagramAccount(instagramAccount);
        currentUser.setAboutMe(aboutMe);
        userRepo.save(currentUser);
        logger.log(Log.LOG_USER_SELF_UPDATE.toString(), LoggerConstant.USER_UPDATED_LOG + currentUsername + PROCESS_OWNER + currentUsername);
        return new SuccessResult(USER_UPDATED);
    }

    @Override
    public Result changePassword(String username, String newPassword) {
        User user = findUserByUsername(username).getData();
        if (!validation.validateChangePassword(user, newPassword).isSuccess()) {
            return new ErrorResult(validation.validateChangePassword(user, newPassword).getMessage());
        }
        user.setPassword(encodePassword(newPassword));
        userRepo.save(user);
        logger.log(Log.LOG_CHANGE_PASSWORD.toString(), LoggerConstant.CHANGE_PASSWORD_LOG + PROCESS_OWNER +
                username);
        return new SuccessResult(PASSWORD_CHANGE);

    }

    @Override
    public Result changeEmail(String currentUsername, String newEmail) {
        User user = findUserByUsername(currentUsername).getData();
        if (!validation.validateChangeEmail(user, currentUsername, newEmail).isSuccess()) {
            return new ErrorResult(validation.validateChangeEmail(user, currentUsername, newEmail).getMessage());
        }
        user.setEmail(newEmail);
        userRepo.save(user);
        logger.log(Log.LOG_CHANGE_EMAIL.toString(),
                user.getUsername() + LoggerConstant.CHANGE_EMAIL_LOG + newEmail + PROCESS_OWNER + currentUsername);
        return new SuccessResult(EMAIL_UPDATED);
    }

    @Override
    public Result changeUsername(String currentUsername, String newUsername) {
        User user = findUserByUsername(currentUsername).getData();
        if (!validation.validateChangeUsername(user, currentUsername, newUsername).isSuccess()) {
            return new ErrorResult(validation.validateChangeUsername(user, currentUsername, newUsername).getMessage());
        }

        user.setUsername(newUsername);
        userRepo.save(user);
        logger.log(Log.LOG_CHANGE_USERNAME.toString(), currentUsername + user.getUsername() + LoggerConstant.CHANGE_USERNAME_LOG + newUsername + PROCESS_OWNER +
                currentUsername);
        return new SuccessResult(USERNAME_UPDATED);
    }

    //dtos
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
    public DataResult<List<User>> populerPoets() {

        return null;
    }

    @Override
    public DataResult<List<User>> mostPoemOwners() {
        List<User> users = userRepo.findAll();
        if (users.size() < 5) {
            return new ErrorDataResult<>();
        }
        users.sort(Comparator.comparing(User::getPoemCounts).reversed());
        users.subList(0, 4);
        return new SuccessDataResult<>(users, "");
    }

    //Helpers
    private void makeFirstFollower(String firstName, String email, User user, String password) throws MessagingException {
        userRepo.save(user);
        Follower firstFollower = new Follower();
        firstFollower.setFollowTime(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));
        firstFollower.setFrom(user);
        firstFollower.setTo(user);
        followersRepo.save(firstFollower);
        emailService.sendNewPasswordEmail(firstName, password, email);
    }

    private void deleteUsersData(User user) {
        privateMessageRepo.deleteAll(privateMessageRepo.findAllByFrom(user));
        privateMessageRepo.deleteAll(privateMessageRepo.findAllByTo(user));
        noticeRepo.deleteAll(noticeRepo.findAllByUser(user));
        followersRepo.deleteAll(followersRepo.findAllByFrom(user));
        followersRepo.deleteAll(followersRepo.findAllByTo(user));
        likedPoemsRepo.deleteAll(likedPoemsRepo.findByUser(user));

        for (Poem deletedPoem : user.getPoems()) {
            likedPoemsRepo.deleteAll(deletedPoem.getLikedUsers());
            poemCommentRepo.deleteAll(deletedPoem.getPoemComments());
        }
        poemRepo.deleteAll(user.getPoems());
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

    @Override
    public void updateUserLoginDate(String username) {
        User user = findUserByUsername(username).getData();
        user.setLastLoginDate(LocalDateTime.now().atZone(ZoneId.of("UTC+3")));
        userRepo.save(user);

    }


}

