package com.poembook.poembook.auth.configuration;

import com.poembook.poembook.auth.domain.UserPrincipal;
import com.poembook.poembook.entities.users.User;
import com.poembook.poembook.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service("userDetailsService")
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepo userRepo;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepo.findUserByUsername(username);
            return new UserPrincipal(userRepo.findUserByUsername(username));
        } catch (NoSuchElementException e) {
            throw new UsernameNotFoundException("User " + username + " not found.", e);
        }
    }

}
