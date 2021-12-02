package com.poembook.poembook.repository;

import com.poembook.poembook.entities.users.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepo extends JpaRepository<PasswordReset, Long> {
    PasswordReset findByCode(String code);
}
