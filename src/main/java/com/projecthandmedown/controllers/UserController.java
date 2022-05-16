package com.projecthandmedown.controllers;
import com.projecthandmedown.models.*;
import com.projecthandmedown.repositories.*;
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
import java.util.List;
import java.util.Locale;

@Controller
public class UserController {
    private final UserRepository userDao;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SendGridEmailService sendGridEmailService;
    private final RoleRepository roles;
    private final UserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ListingRepository listingDao;
    private final ForumPostRepository forumPostDao;
    private final ForumReplyRepository forumReplyDao;
    private final ActivityRepository activityDao;
    private final AdminDeletedEmailRepository adminDeletedEmailDao;

    boolean emailOnList = false;


    public UserController(UserRepository userDao, PasswordEncoder passwordEncoder, EmailService emailService, SendGridEmailService sendGridEmailService, RoleRepository roles, UserService userService, PasswordResetTokenRepository passwordResetTokenRepository, ListingRepository listingDao, ForumPostRepository forumPostDao, ForumReplyRepository forumReplyDao, ActivityRepository activityDao, AdminDeletedEmailRepository adminDeletedEmailDao) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.sendGridEmailService = sendGridEmailService;
        this.roles = roles;
        this.userService = userService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.listingDao = listingDao;
        this.forumPostDao = forumPostDao;
        this.forumReplyDao = forumReplyDao;
        this.activityDao = activityDao;
        this.adminDeletedEmailDao = adminDeletedEmailDao;
    }

    @Value("${filestack.api.key}")
    private String filestackKey;

    @GetMapping("/sign-up")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("filestackKey", filestackKey);
        if(emailOnList){
            model.addAttribute("alert", true);
            model.addAttribute("message", "Can't sign up with that email as a previous account with that email was deleted for misconduct.");
            emailOnList = false;
        }
        return "users/sign-up";
    }

    @PostMapping("/sign-up")
    public String saveUser(@ModelAttribute User user) {
        String hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);
        user.setUserIsAdmin(false);
        List<AdminDeletedEmail> emailList = adminDeletedEmailDao.findAll();
        for(int i = 0; i < emailList.size(); i++){
            if(user.getEmail().equals(emailList.get(i).getEmail())){
                emailOnList = true;
                return "redirect:/sign-up";
            }
        }


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
        model.addAttribute("who", "user");
        model.addAttribute("url", "/messaging/" + listingId + "/" + userToSend.getId());
        return "users/messaging";
    }

    @PostMapping("/messaging/{listingId}/{userId}")
    public String sendMessage(@ModelAttribute Message message, @PathVariable long listingId, @PathVariable long userId) throws IOException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        message.setSender(loggedInUser);
        User receiver = userDao.getById(userId);
        message.setReceiver(receiver.getEmail());
        sendGridEmailService.sendTextEmail(message, listingId);
        String redirect = "redirect:/listing/" + listingId;
        return redirect;
    }

    @GetMapping("/report/{type}/{id}")
    public String sendReport(Model model, @PathVariable String type, @PathVariable long id) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("type", type);
        model.addAttribute("id", id);
        model.addAttribute("message", new Message());
        if(type.equals("listing")){
            Listing listing = listingDao.getById(id);
            model.addAttribute("object", listing);
            model.addAttribute("objectType", "post");
            model.addAttribute("postType", "listing");
        }else if(type.equals("forum_post")){
            ForumPost forumPost = forumPostDao.getById(id);
            model.addAttribute("object", forumPost);
            model.addAttribute("objectType", "post");
            model.addAttribute("postType", "forum post");
        }else if(type.equals("forum_reply")){
            ForumReply forumReply = forumReplyDao.getById(id);
            model.addAttribute("object", forumReply);
            model.addAttribute("objectType", "reply");
        }else if(type.equals("activity")){
            Activity activity = activityDao.getById(id);
            model.addAttribute("object", activity);
            model.addAttribute("objectType", "post");
            model.addAttribute("postType", "activity");
        }else {
            User user = userDao.getUserById(id);
            model.addAttribute("object", user);
            model.addAttribute("objectType", "user");
        }
        return "users/report";
    }

    @PostMapping("/report/{type}/{id}")
    public String sendReport(@ModelAttribute Message message, @PathVariable String type, @PathVariable long id) throws IOException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        message.setSender(loggedInUser);
        if(type.equals("listing")){
            Listing listing = listingDao.getById(id);
            sendGridEmailService.sendReportEmail(message, listing);
        }else if(type.equals("forum_post")){
            ForumPost forumPost = forumPostDao.getById(id);
            sendGridEmailService.sendReportEmail(message, forumPost);
        }else if(type.equals("forum_reply")){
            ForumReply forumReply = forumReplyDao.getById(id);
            sendGridEmailService.sendReportEmail(message, forumReply);
        }else if(type.equals("activity")){
            Activity activity = activityDao.getById(id);
            sendGridEmailService.sendReportEmail(message, activity);
        }else {
            User user = userDao.getUserById(id);
            sendGridEmailService.sendReportEmail(message, user);
        }
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

    @GetMapping("/admin/users")
    public String showAllUsers(Model model){
        List<User> users = userDao.findAll();
        List<UserRole> roleList = roles.findAll();
        model.addAttribute("users", users);
        model.addAttribute("userRoles", roleList);
        return "users/users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable long id){
        User user = userDao.getUserById(id);
        AdminDeletedEmail deletedEmail = new AdminDeletedEmail(user.getEmail());
        adminDeletedEmailDao.save(deletedEmail);
        userDao.delete(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/message/{id}")
    public String adminSendMessage(Model model, @PathVariable long id){
        User userToSend = userDao.getUserById(id);
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("messageReceiver", userToSend);
        model.addAttribute("message", new Message());
        model.addAttribute("url", "/admin/users/message/" + userToSend.getId());
        return "users/messaging";
    }

    @PostMapping ("/admin/users/message/{id}")
    public String adminSendMessage(@ModelAttribute Message message, @PathVariable long id) throws IOException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        message.setSender(loggedInUser);
        User receiver = userDao.getById(id);
        message.setReceiver(receiver.getEmail());
        sendGridEmailService.sendAdminEmail(message);
        return "redirect:/admin/users";
    }

    @GetMapping("/user/admin/message/{id}")
    public String userSendAdminMessage(Model model, @PathVariable long id) {
        User repliedToAdmin = userDao.getUserById(id);
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("messageReceiver", repliedToAdmin);
        model.addAttribute("message", new Message());
        model.addAttribute("url", "/user/admin/message/" + repliedToAdmin.getId());
        return "users/messaging";
    }

    @PostMapping ("/user/admin/message/{id}")
    public String userSendAdminMessage(@ModelAttribute Message message, @PathVariable long id) throws IOException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        message.setSender(loggedInUser);
        User receiver = userDao.getById(id);
        message.setReceiver(receiver.getEmail());
        sendGridEmailService.userSendAdminEmail(message, receiver);
        return "redirect:/";
    }

    @GetMapping("/admin/users/change/{type}/{id}")
    public String makeUserAdmin(@PathVariable String type,@PathVariable long id){
        User userToAdmin = userDao.getUserById(id);
        UserRole role = roles.getUserRoleByUserId(id);
        role.setRole(type.toUpperCase(Locale.ROOT));
        roles.save(role);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/strike/{id}")
    public String addStrike(@PathVariable long id){
        User user = userDao.getUserById(id);
        user.increaseStrikes();
        userDao.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/remove/strike/{id}")
    public String subtractStrike(@PathVariable long id){
        User user = userDao.getUserById(id);
        user.decreaseStrikes();
        userDao.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/email_list")
    public String showEmailList(Model model){
        List<AdminDeletedEmail> emailList =adminDeletedEmailDao.findAll();
        model.addAttribute("emailList", emailList);
        return "users/admin-deleted-email-list";
    }

    @GetMapping("/admin/email_list/remove/{id}")
    public String removeFromEmailList(@PathVariable long id){
        AdminDeletedEmail email = adminDeletedEmailDao.getById(id);
        adminDeletedEmailDao.delete(email);
        return "redirect:/admin/email_list";
    }

}
