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

import java.util.ArrayList;
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


    @GetMapping("activities/search")
    public String filteredActivities (Model model, @RequestParam String keyword) {



        model.addAttribute("keyword", keyword);
        List<Activity> activities = activityDao.findAll();
        List<Activity> filteredActivities = new ArrayList<>();

        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            String title = activity.getTitle();
            if (title.contains(keyword)) {
                System.out.println(title);
                filteredActivities.add(activity);
            }

        }
        model.addAttribute("activities",filteredActivities);
        return "activities/ActivityFiltered";
    }
}







