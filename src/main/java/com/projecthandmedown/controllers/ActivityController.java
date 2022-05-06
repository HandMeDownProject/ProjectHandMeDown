package com.projecthandmedown.controllers;
import com.projecthandmedown.models.Activity;
import com.projecthandmedown.models.User;
import com.projecthandmedown.repositories.ActivityRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ActivityController {

    private final ActivityRepository activityDao;
    private final UserRepository userDAO;
    private final EmailService emailService;

    public ActivityController(ActivityRepository activityDao, UserRepository userDAO, EmailService emailService) {
        this.activityDao = activityDao;
        this.emailService = emailService;
        this.userDAO = userDAO;
    }

    @GetMapping("/activities")
    public String activitiesView(Model model){

       List<Activity> activities =activityDao.findAll();
       model.addAttribute("activities",activities);

        return "activities/activitiesView";


    }
    @GetMapping("/activities/{id}")
    public String activityView(Model model, @PathVariable Long id){
        Activity activity = activityDao.getById(id);
        model.addAttribute("activity",activity);


        return "activities/activityView";
    }


    @Value("${filestack.api.key}")
    private String filestackKey;

    @GetMapping("/activities/create")
    public String createActivity(Model model){
        model.addAttribute("activity", new Activity());
        model.addAttribute("filestackKey", filestackKey);
        //model.addAttribute("user", new User());

        return "activities/activityCreate";

    }

    @PostMapping("/activities/create")
    public String addActivity(@ModelAttribute Activity activity,        RedirectAttributes attr
    ){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        activity.setUser(user); // <-- this will be setting     user for post.

        if(activity.getTitle().equals("") || activity.getBody().equals("")){
            return "activities/activityCreate";
        }
        activityDao.save(activity);
        attr.addFlashAttribute("createMsg","Successfully added a new post");


        emailService.prepareAndSendActivity(activity,activity.getTitle(),activity.getBody());


        return "redirect:/activities";
    }


    @GetMapping("/activities/{id}/edit")
    public String editPost(@PathVariable Long id, Model model){
        Activity activity = activityDao.getById(id);

        model.addAttribute("activity",activity);
        return "activities/activityEdit";

    }

    @PostMapping("activities/edit")
    public String editAndSubmit(@ModelAttribute Activity activity){


        activityDao.save(activity);
        return "redirect:/activities";
    }

    @GetMapping ("activities/{id}/delete")
    public String deleteActivity(@PathVariable Long id,Model model, RedirectAttributes attr){
        Activity activity = activityDao.getById(id);
        activityDao.delete(activity);
        attr.addFlashAttribute("deleteMsg","Successfully deleted the post");
        return "redirect:/activities";
    }

    @GetMapping("activities/user/{user_id}")
    public String seeAllUserPosts(@PathVariable Long user_id,Model model){
        User targetUser = userDAO.getUserById(user_id);
        List<Activity> activities = activityDao.getByUser(targetUser);
        model.addAttribute("activities",activities);

        return"activities/activityUserPosts";
    }

//
//    @GetMapping("/users/{id}")
////    @ResponseBody
//    public String userID(@PathVariable long id, Model model) {
//        User currentUser = userDAO.getUserById(id);
//        List<Post> posts = postDao.getByUser(currentUser);
//        model.addAttribute("user", currentUser);
//        model.addAttribute("posts", posts);
////        System.out.println("currentUser = " + currentUser.getUsername() + " " + currentUser.getEmail());
////        System.out.println("posts = " + posts);
//        return "show_user";
//    }
//





//    @GetMapping("/posts/create")
////    @ResponseBody
//    public String create(Model model) {
//        model.addAttribute("post", new Post());
//        model.addAttribute("user", new User());
//        return "posts/create";
//    }
//
//    @PostMapping("/posts/create")
//    public String post(@ModelAttribute Post post) {
//        post.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        if(post.getTitle().equals("") || post.getBody().equals("")){
//            return "posts/create";
//        }
//        postDao.save(post);
//        emailService.prepareAndSend(post, "post created", "Confirmation: your post has been created");
//        return "redirect:/";
//    }




//    @GetMapping("/activities/{id}/edit")
//    public String editActivity(@PathVariable Long id, Model model) {
//        Activity activity = activityDao.getById(id);
//
//        model.addAttribute("activity", activity);
//        return "";
//    }

//    public PostController(PostRepository postDao, UserRepository userDAO) {
//        this.postDao = postDao;
//        this.userDAO = userDAO;
//    }

//    @GetMapping("/")
////    @ResponseBody
//    public String posts(Model model) {
//        model.addAttribute("posts", postDao.findAll());
//        return "posts/index";
//    }
//
//    @GetMapping("/posts/{id}")
////    @ResponseBody
//    public String postID(@PathVariable long id, Model model) {
//        model.addAttribute("posts", postDao.findAllById(Collections.singleton(id)));
//        return "posts/show";
//    }
//
//    @GetMapping("/users/{id}")
////    @ResponseBody
//    public String userID(@PathVariable long id, Model model) {
//        User currentUser = userDAO.getUserById(id);
//        List<Post> posts = postDao.getByUser(currentUser);
//        model.addAttribute("user", currentUser);
//        model.addAttribute("posts", posts);
////        System.out.println("currentUser = " + currentUser.getUsername() + " " + currentUser.getEmail());
////        System.out.println("posts = " + posts);
//        return "show_user";
//    }
//
//    @GetMapping("/users")
////    @ResponseBody
//    public String users(Model model) {
//        model.addAttribute("users", userDAO.findAll());
//        return "users";
//    }
//
//    @GetMapping("/posts/create")
////    @ResponseBody
//    public String create(Model model) {
//        model.addAttribute("post", new Post());
//        model.addAttribute("user", new User());
//        return "posts/create";
//    }
//
//    @PostMapping("/posts/create")
//    public String post(@ModelAttribute Post post) {
//        post.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        if(post.getTitle().equals("") || post.getBody().equals("")){
//            return "posts/create";
//        }
//        postDao.save(post);
//        emailService.prepareAndSend(post, "post created", "Confirmation: your post has been created");
//        return "redirect:/";
//    }
//
//    @GetMapping("posts/{id}/edit")
////    @ResponseBody
//    public String edit(@PathVariable long id, Model model) {
//        model.addAttribute("post", postDao.getById(id));
////        model.addAttribute("user", new User());
//        return "edit";
//    }
//
//    @PostMapping("/edit")
//    public String edit(@ModelAttribute Post post) {
//        postDao.save(post);
//        return "redirect:/";
//    }
//
//    @GetMapping("posts/{id}/delete")
//    public String delete(@PathVariable long id, Model model) {
//        Post post = postDao.getById(id);
//        postDao.delete(post);
//        return "redirect:/";
//    }
//
//    @GetMapping("/error")
//    public String error(){
//        return "/error/500.html";
//    }


}

