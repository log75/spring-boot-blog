package com.log75.blog.service.dao;

import com.log75.blog.model.PasswordResetToken;
import com.log75.blog.model.Role;
import com.log75.blog.model.User;
import com.log75.blog.model.VerificationToken;
import com.log75.blog.repository.PasswordTokenRepository;
import com.log75.blog.repository.RoleRepository;
import com.log75.blog.repository.UserRepository;
import com.log75.blog.repository.VerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDao {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private VerificationRepository verificationRepository;
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    public UserDao(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, VerificationRepository verificationRepository, PasswordTokenRepository passwordTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationRepository = verificationRepository;
        this.passwordTokenRepository = passwordTokenRepository;
    }

    @Transactional
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName("USER"));
        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUser(String verificationToken) {
        User user = verificationRepository.findByToken(verificationToken).getUser();
        return user;
    }


    public VerificationToken getVerificationToken(String VerificationToken) {
        return verificationRepository.findByToken(VerificationToken);
    }

    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        verificationRepository.save(myToken);
    }


    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken" : isTokenExpired(passToken) ? "expired" : null;
    }

    private static boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private static boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public Optional<User> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token) .getUser());
    }
}
