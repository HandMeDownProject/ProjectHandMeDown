package com.projecthandmedown.controllers;
import com.projecthandmedown.models.Message;
import com.projecthandmedown.models.User;
import com.projecthandmedown.models.UserRole;
import com.projecthandmedown.repositories.RoleRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import com.projecthandmedown.services.SendGridEmailService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Optional;

@Controller
public class UserController {
    private UserRepository userDao;
    private PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SendGridEmailService sendGridEmailService;
    private final RoleRepository roles;

    public UserController(UserRepository userDao, PasswordEncoder passwordEncoder, EmailService emailService, SendGridEmailService sendGridEmailService, RoleRepository roles) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.sendGridEmailService = sendGridEmailService;
        this.roles = roles;
    }

    @GetMapping("/sign-up")
    public String showSignupForm(Model model){
        model.addAttribute("user", new User());
        return "users/sign-up";
    }

    @PostMapping("/sign-up")
    public String saveUser(@ModelAttribute User user){
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
    public String showUserProfile(Model model){
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User fromDao = userDao.getUserById(loggedInUser.getId());
        model.addAttribute("user", fromDao);
        if(fromDao.getUserIsAdmin()){
            return "users/admin";
        }
        return "users/profile";
    }

    @PostMapping("/profile")
    public String editUser(@ModelAttribute User user, Model model){

        String hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);
        user.setUserIsAdmin(false);
        userDao.save(user);
        model.addAttribute("user", user);

//        UserRole newUser = new UserRole(user.getId(), "USER");
//
//        roles.save(newUser);
//        if(userDao.getUserById(user.getId()).getUserIsAdmin()){
//            return "redirect:users/admin";
//        }else {

            return "redirect:/profile";
//        }
    }

    @GetMapping("/admin")
    public String showAdminPage(Model model){
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User fromDao = userDao.getUserById(loggedInUser.getId());
        model.addAttribute("user", fromDao);
        return "users/admin";
    }

    @GetMapping("/messaging/{listingId}/{userId}")
    public String sendUserMessage(Model model, @PathVariable long listingId, @PathVariable long userId){
        User userToSend = userDao.getUserById(userId);
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("messageReceiver", userToSend);
        model.addAttribute("message", new Message());
        model.addAttribute("listingId", listingId);
        return "users/messaging";
    }

    @PostMapping ("/messaging/{listingId}/{userId}")
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
    public String sendReport(Model model){
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("message", new Message());
        return "users/report";
    }

    @PostMapping ("/report")
    public String sendReport(@ModelAttribute Message message){
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        message.setSender(loggedInUser);
        message.setReceiver("admin@mail.com");
        emailService.prepareAndSend(message, "New Report", message.getBody());
        return "redirect:/";
    }
}
