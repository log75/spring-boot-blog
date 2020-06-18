package com.log75.blog.repository;

import com.log75.blog.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by alireza on 5/13/20.
 */
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

}
