package com.poembook.poembook.auth.service;

import com.poembook.poembook.auth.configuration.UserDetailsServiceImpl;
import com.poembook.poembook.auth.domain.UserPrincipal;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

import static com.poembook.poembook.constant.UserConstant.FOUND_USER_BY_USERNAME;
import static com.poembook.poembook.constant.UserConstant.NO_USER_FOUND_BY_USERNAME;

@Service
@Transactional
@Qualifier("userDetailsService")
public class AuthService extends UserDetailsServiceImpl {
    private final UserRepo userRepo;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final LoginAttemptService loginAttemptService;

    public AuthService(UserRepo userRepo, UserRepo userRepo1, LoginAttemptService loginAttemptService) {
        super(userRepo);
        this.userRepo = userRepo1;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDate(new Date());
            userRepo.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getUsername()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
