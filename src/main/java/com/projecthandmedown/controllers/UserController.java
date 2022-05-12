package com.projecthandmedown.controllers;
import com.projecthandmedown.models.*;
import com.projecthandmedown.repositories.PasswordResetTokenRepository;
import com.projecthandmedown.repositories.RoleRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import com.projecthandmedown.services.SendGridEmailService;


import com.projecthandmedown.services.UserService;
import net.bytebuddy.utility.RandomString;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class UserController {
    private UserRepository userDao;
    private PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SendGridEmailService sendGridEmailService;
    private final RoleRepository roles;
    private final UserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public UserController(UserRepository userDao, PasswordEncoder passwordEncoder, EmailService emailService, SendGridEmailService sendGridEmailService, RoleRepository roles, UserService userService, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.sendGridEmailService = sendGridEmailService;
        this.roles = roles;
        this.userService = userService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Value("${filestack.api.key}")
    private String filestackKey;

    @GetMapping("/sign-up")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("filestackKey", filestackKey);
        return "users/sign-up";
    }

    @PostMapping("/sign-up")
    public String saveUser(@ModelAttribute User user) {
        String hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);
        user.setUserIsAdmin(false);
        userDao.save(user);
        User justCreatedUser = userDao.findByUsername(user.getUsername());
        UserRole newUser = new UserRole(justCreatedUser.getId(), "USER");
        roles.save(newUser);
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User fromDao = userDao.getUserById(loggedInUser.getId());
        model.addAttribute("user", fromDao);
        model.addAttribute("filestackKey", filestackKey);
        UserRole userRole = roles.getUserRoleByUserId(loggedInUser.getId());
        if (userRole.getRole().equals("ADMIN")) {
            return "/users/admin";
        } else {

            return "/users/profile";
        }
    }

    @PostMapping("/profile")
    public String editUser(@ModelAttribute User user, Model model) {

        String hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);
        user.setUserIsAdmin(false);

        userDao.save(user);
        model.addAttribute("user", user);

//        UserRole newUser = new UserRole(user.getId(), "USER");
//
//        roles.save(newUser);
//        TODO get userRoles var for conditional rendering of pages
        System.out.println("user role:" + " " + roles.getUserRoleByUserId(user.getId()));
        UserRole userRole = roles.getUserRoleByUserId(user.getId());
        if (userRole.getRole().equals("ADMIN")) {
            return "redirect:/admin";
        } else {

            return "redirect:/profile";
        }
    }

    @GetMapping("/admin")
    public String showAdminPage(Model model) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User fromDao = userDao.getUserById(loggedInUser.getId());
        model.addAttribute("user", fromDao);
        model.addAttribute("filestackKey", filestackKey);
        return "users/admin";
    }

    @GetMapping("/messaging/{listingId}/{userId}")
    public String sendUserMessage(Model model, @PathVariable long listingId, @PathVariable long userId) {
        User userToSend = userDao.getUserById(userId);
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("messageReceiver", userToSend);
        model.addAttribute("message", new Message());
        model.addAttribute("listingId", listingId);
        return "users/messaging";
    }

    @PostMapping("/messaging/{listingId}/{userId}")
    public String sendMessage(@ModelAttribute Message message, @PathVariable long listingId, @PathVariable long userId) throws IOException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        message.setSender(loggedInUser);
        User receiver = userDao.getById(userId);
        message.setReceiver(receiver.getEmail());
        emailService.prepareAndSend(message, "New Message", message.getBody());
        sendGridEmailService.sendTextEmail(message);
        String redirect = "redirect:/listing/" + listingId;
        return redirect;
    }

    @GetMapping("/report")
    public String sendReport(Model model) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("message", new Message());
        return "users/report";
    }

    @PostMapping("/report")
    public String sendReport(@ModelAttribute Message message) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        message.setSender(loggedInUser);
        message.setReceiver("admin@mail.com");
        emailService.prepareAndSend(message, "New Report", message.getBody());
        return "redirect:/";
    }

    @GetMapping("/forgot_password")
    public String forgotPassword(Model model) {
        model.addAttribute("message", "a link to reset your password");
        model.addAttribute("url", "forgot_password");
        return "users/forgot-password";
    }

    @PostMapping("/forgot_password")
    public String forgotPassword(@RequestParam(name = "email") String email, HttpServletRequest request, Model model) throws IOException {
        User user = userDao.getUserByEmail(email);
        String token = RandomString.make(30);
        userService.createPasswordResetTokenForUser(user, token);
        sendGridEmailService.sendTextEmail(user, token);
        return "redirect:/";
    }

    @GetMapping("/reset_password")
    public String resetPassword(@RequestParam(name = "token") String token, Model model) {
        String result = userService.validatePasswordResetToken(token);
        if (result != null) {
            return "redirect:/login";
        } else {
            ResetPassword resetPassword = new ResetPassword();
            resetPassword.setToken(token);
            model.addAttribute("resetPassword", resetPassword);
            return "users/reset-password";
        }
    }

    @PostMapping("/reset_password")
    public String resetPassword(@ModelAttribute ResetPassword resetPassword, Model model) {
        if (resetPassword.getPassword().equals(resetPassword.getConfirmPassword())) {
            PasswordResetToken pass = passwordResetTokenRepository.findByToken(resetPassword.getToken());
            User user = pass.getUser();
            String hash = passwordEncoder.encode(resetPassword.getPassword());
            user.setPassword(hash);
            userDao.save(user);
            passwordResetTokenRepository.delete(pass);
        }
        return "redirect:/login";
    }

    @GetMapping("/forgot_username")
    public String forgotUsername(Model model) {
        model.addAttribute("message", "your username with a link to login");
        model.addAttribute("url", "forgot_username");        return "users/forgot-password";
    }

    @PostMapping("/forgot_username")
    public String forgotUsername(@RequestParam(name = "email") String email, HttpServletRequest request, Model model) throws IOException {
        User user = userDao.getUserByEmail(email);
        sendGridEmailService.sendTextEmail(user);
        return "redirect:/";
    }

}
