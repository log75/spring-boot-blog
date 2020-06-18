package com.log75.blog.controller;

import com.log75.blog.exception.UsernameNotFoundExceptions;
import com.log75.blog.model.PasswordDto;
import com.log75.blog.model.User;
import com.log75.blog.model.VerificationToken;
import com.log75.blog.service.dao.UserDao;
import com.log75.blog.security.SecurityService;
import com.log75.blog.security.verification.GenericResponse;
import com.log75.blog.security.verification.OnRegistrationCompleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UserController {

    private UserDao userDao;
    private SecurityService securityService;
    private ApplicationEventPublisher applicationEventPublisher;
    private JavaMailSender javaMailSender;
    private String pass;

    @Autowired
    public UserController(UserDao userDao, SecurityService securityService, ApplicationEventPublisher applicationEventPublisher, JavaMailSender javaMailSender) {
        this.userDao = userDao;
        this.securityService = securityService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.javaMailSender = javaMailSender;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute("userForm") User userForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        //userValidator.validate(userForm, bindingResult);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/registration";
        }

        if (!(userForm.getPassword().equals(userForm.getPasswordConfirm()))) {
            redirectAttributes.addFlashAttribute("errorsP", "password not match");
            return "redirect:/registration";
        }

        if (userDao.findByUsername(userForm.getUsername()) != null) {
            redirectAttributes.addFlashAttribute("errorsU", "user name already exist");
            return "redirect:/registration";
        }
        pass = userForm.getPassword();
        User registered = userDao.save(userForm);
        String appUrl = request.getContextPath();
        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
        return "verification";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(Model model, @RequestParam("token") String token) {

        VerificationToken verificationToken = userDao.getVerificationToken(token);
        if (verificationToken == null) {
            String message = "auth.message.invalidToken";
            model.addAttribute("message", message);
            return "redirect:/badUser.html";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = "auth.message.expired";
            model.addAttribute("message", messageValue);
            return "redirect:/badUser.html";
        }
        user.setEnabled(true);
        userDao.saveRegisteredUser(user);
        securityService.autoLogin(user.getUsername(), pass);
        return "redirect:/welcome";
    }

    @PostMapping("/resetPassword")
    public GenericResponse resetPassword(HttpServletRequest request,
                                         @RequestParam("email") String userEmail) {
        User user = userDao.findByUsername(userEmail);
        if (user == null) {
            throw new UsernameNotFoundExceptions();
        }
        String token = UUID.randomUUID().toString();
        userDao.createPasswordResetTokenForUser(user, token);
        javaMailSender.send(constructResetTokenEmail(getAppUrl(request), token, user));
        return new GenericResponse("message.resetPasswordEmail", null);
    }

    @GetMapping("/changePassword")
    public String showChangePasswordPage(Model model, @RequestParam("token") String token) {
        String result = userDao.validatePasswordResetToken(token);
        if (result != null) {
            String message = "error";
            return "redirect:/login" + "?message=" + message;
        } else {
            model.addAttribute("token", token);
            return "redirect:/updatePassword";
        }
    }

    @PostMapping("/savePassword")
    public GenericResponse savePassword(@Valid PasswordDto passwordDto) {

        String result = userDao.validatePasswordResetToken(passwordDto.getToken());

        if (result != null) {
            return new GenericResponse("auth.message." + result);
        }

        Optional user = userDao.getUserByPasswordResetToken(passwordDto.getToken());
        if (user.isPresent()) {
            userDao.changeUserPassword((User) user.get(), passwordDto.getNewPassword());
            return new GenericResponse("message.resetPasswordSuc");
        } else {
            return new GenericResponse("auth.message.invalid");
        }
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword() {
        return "forgotPassword";
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private SimpleMailMessage constructResetTokenEmail(String contextPath, String token, User user) {
        String url = contextPath + "/changePassword?token=" + token;
        String message = "Reset Password";
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getUsername());
        email.setSubject(subject);
        email.setText(body);
        return email;
    }

}
