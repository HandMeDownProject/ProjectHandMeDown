package com.projecthandmedown.controllers;
import com.projecthandmedown.models.Activity;
import com.projecthandmedown.models.ActivityCategory;
import com.projecthandmedown.models.Listing;
import com.projecthandmedown.models.User;
import com.projecthandmedown.repositories.ActivityCategoryRepository;
import com.projecthandmedown.repositories.ActivityRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class ActivityController {

    private final ActivityRepository activityDao;
    private final UserRepository userDAO;
    private final EmailService emailService;
    private final ActivityCategoryRepository activityCatDao;

    public ActivityController(ActivityRepository activityDao, UserRepository userDAO, EmailService emailService, ActivityCategoryRepository activityCatDao) {
        this.activityDao = activityDao;
        this.emailService = emailService;
        this.userDAO = userDAO;
        this.activityCatDao = activityCatDao;
    }

    @GetMapping("/activities")
    public String activitiesView(Model model) {
        List<Activity> activities = activityDao.findAll(Sort.by(Sort.Direction.DESC, "id"));
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDAO.getUserById(loggedInUser.getId());
        List<Activity> filteredList = new ArrayList<>();
        for(int i = 0; i < activities.size(); i++){
            if(activities.get(i).getUser().getUserLocationState().equals(user.getUserLocationState())){
                if(activities.get(i).getUser().getUserLocation().equals(user.getUserLocation())){
                    filteredList.add(activities.get(i));
                }
            }
        }
        model.addAttribute("activities", filteredList);
        model.addAttribute("user", user);
        return "activities/activitiesView";
    }

    @GetMapping("/activities/{id}")
    public String activityView(Model model, @PathVariable Long id) {
        Activity activity = activityDao.getById(id);
        activity.increaseViewCount();
        activityDao.save(activity);
        model.addAttribute("activity", activity);


        return "activities/activityView";
    }

    @GetMapping("/activities/categories/{id}")
    public String viewByCategory(Model model, @PathVariable Long id) {
        List<ActivityCategory> categories = activityCatDao.findAll();
        List<Activity> activities = activityCatDao.getById(id).getActivities();
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDAO.getUserById(loggedInUser.getId());
        List<Activity> filteredList = new ArrayList<>();
        for(int i = 0; i < activities.size(); i++){
            if(activities.get(i).getUser().getUserLocationState().equals(user.getUserLocationState())){
                if(activities.get(i).getUser().getUserLocation().equals(user.getUserLocation())){
                    filteredList.add(activities.get(i));
                }
            }
        }
        model.addAttribute("activities", filteredList);
        model.addAttribute("user", user);
        model.addAttribute("categories", categories);
        return "activities/activitiesView";
    }


    @Value("${filestack.api.key}")
    private String filestackKey;

    @GetMapping("/activities/create")
    public String createActivity(Model model) {
        model.addAttribute("activity", new Activity());
        model.addAttribute("filestackKey", filestackKey);
        List<ActivityCategory> categories = activityCatDao.findAll();
        model.addAttribute("categories", categories);


        return "activities/activityCreate";
    }

    @PostMapping("/activities/create")

    public String addActivity(@ModelAttribute Activity activity, RedirectAttributes attr, Model model

    ) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        activity.setUser(user); // <-- this will be setting     user for post.


        if (activity.getTitle().equals("") || activity.getBody().equals("")) {
            return "activities/activityCreate";
        }

        String date = new String(String.valueOf(new Date(System.currentTimeMillis())));
        activity.setTimestamp(date);
        activity.setViewCount(0L);

        activityDao.save(activity);


        attr.addFlashAttribute("createMsg", "Successfully added a new post");


        emailService.prepareAndSendActivity(activity, activity.getTitle(), activity.getBody());


        return "redirect:/activities";
    }


    @GetMapping("/activities/{id}/edit")
    public String editPost(@PathVariable Long id, Model model) {
        Activity activity = activityDao.getById(id);

        List<ActivityCategory> categories = activityCatDao.findAll();


        model.addAttribute("activity", activity);
        model.addAttribute("categories", categories);

        return "activities/activityEdit";

    }

    @PostMapping("activities/edit")
    public String editAndSubmit(@ModelAttribute Activity activity) {


        activityDao.save(activity);
        return "redirect:/activities";
    }

    @GetMapping("activities/{id}/delete")
    public String deleteActivity(@PathVariable Long id, Model model, RedirectAttributes attr) {
        Activity activity = activityDao.getById(id);
        activity.getActivityCategories().clear();
        activityDao.delete(activity);
        attr.addFlashAttribute("deleteMsg", "Successfully deleted the post");
        return "redirect:/activities";
    }

    @GetMapping("activities/user/{user_id}")
    public String seeAllUserPosts(@PathVariable Long user_id, Model model) {
        User targetUser = userDAO.getUserById(user_id);
        List<Activity> activities = activityDao.getByUser(targetUser);
        model.addAttribute("activities", activities);

        return "activities/activityUserPosts";
    }


//    checking for duplicate in List
   public static List<Activity> checkDuplicate( List<Activity> lists){
        ArrayList<Activity> arr = new ArrayList<>();
        for (Activity element : lists){
            if(!lists.contains(element)){
                arr.add(element);
            }
        }
        return arr;
    }


    @GetMapping("activities/search")
    public String filteredActivities(Model model, @RequestParam String keyword) {


        model.addAttribute("keyword", keyword.toLowerCase(Locale.ROOT));
        List<Activity> activities = activityDao.findAll();
        List<Activity> filteredActivities = new ArrayList<>();



        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            String title = activity.getTitle();
            String body = activity.getBody();


            if((title.toLowerCase().contains(keyword.toLowerCase())) || (body.toLowerCase().contains(keyword.toLowerCase()))){
                filteredActivities.add(activity); // wil have duplicates.
                checkDuplicate(filteredActivities);
            }
        }



        model.addAttribute("activities", filteredActivities);

        return "activities/ActivityFiltered";
    }


}







