package com.projecthandmedown.controllers;
import com.projecthandmedown.models.Message;
import com.projecthandmedown.models.User;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private UserRepository userDao;
    private PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserController(UserRepository userDao, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model){
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        return "users/profile";
    }

    @GetMapping("/messaging/{id}")
    public String sendUserMessage(Model model, @PathVariable long id){
        User userToSend = userDao.getUserById(id);
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("messageReceiver", userToSend);
        model.addAttribute("message", new Message());
        return "users/messaging";
    }

    @PostMapping ("/messaging/{id}")
    public String sendMessage(@ModelAttribute Message message, @PathVariable long id) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        message.setSender(loggedInUser.getEmail());
        User receiver = userDao.getById(id);
        message.setReceiver(receiver.getEmail());
        emailService.prepareAndSend(message, "New Message", message.getBody());
        return "redirect:/";
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
        message.setSender(loggedInUser.getEmail());
        message.setReceiver("admin@mail.com");
        emailService.prepareAndSend(message, "New Report", message.getBody());
        return "redirect:/";
    }
}

