package com.poembook.poembook.business.abstracts;

import com.poembook.poembook.core.utilities.result.DataResult;
import com.poembook.poembook.core.utilities.result.Result;
import com.poembook.poembook.entities.dtos.profile.ProfileUser;
import com.poembook.poembook.entities.users.User;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {
    Result addUser(String currentUsername,String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive) throws MessagingException;

    Result register(String firstName, String lastName, String username, String email) throws MessagingException;

    Result updateUser(String adminUsername, String userUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive);

    Result selfUpdate(String currentUsername, String newFirstname, String newLastname, String newUsername, String facebookAccount, String twitterAccount, String instagramAccount, String aboutMe);

    Result deleteUser(String username);

    Result resetPassword(String email);

    Result changePassword(String username, String newPassword);

    DataResult<ProfileUser> getUserProfile(String username);

    DataResult<List<User>> getAllUsers();

    DataResult<List<User>> getAllPoets();

    DataResult<List<User>> getAllEditors();

    DataResult<List<User>> getAllAdmins();

    DataResult<List<User>> getAllSuperAdmins();

    DataResult<User> findUserByUsername(String username);

    DataResult<User> findUserById(Long id);

    DataResult<User> findUserByEmail(String email);

    void updatePoemCount(User user);

    void updateUserLoginDate(String username);

    Result changeEmail(String currentUsername, String newEmail);

    Result changeUsername(String currentUsername, String newUsername);

    Result makeSuperAdmin(String username);

}

