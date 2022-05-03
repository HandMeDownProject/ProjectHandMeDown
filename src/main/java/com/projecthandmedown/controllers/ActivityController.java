package com.projecthandmedown.controllers;
import com.projecthandmedown.models.Activity;
import com.projecthandmedown.repositories.ActivityRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("activities/{id}")
    public String activityview(Model model, @PathVariable Long id){


        return "activities/activityView";
    }

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

