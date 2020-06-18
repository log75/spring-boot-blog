package com.log75.blog.repository;

import com.log75.blog.model.User;
import com.log75.blog.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by alireza on 5/13/20.
 */
public interface VerificationRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    VerificationToken findByUser(User user);
}
