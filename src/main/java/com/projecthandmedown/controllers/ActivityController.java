package com.projecthandmedown.controllers;
import com.projecthandmedown.models.Activity;
import com.projecthandmedown.models.ActivityCategory;
import com.projecthandmedown.models.User;
import com.projecthandmedown.repositories.ActivityCategoryRepository;
import com.projecthandmedown.repositories.ActivityRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import com.projecthandmedown.services.UserService;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final UserService userService;

    public ActivityController(ActivityRepository activityDao, UserRepository userDAO, EmailService emailService, ActivityCategoryRepository activityCatDao, UserService userService) {
        this.activityDao = activityDao;
        this.emailService = emailService;
        this.userDAO = userDAO;
        this.activityCatDao = activityCatDao;
        this.userService = userService;
    }

    @GetMapping("/activities")
    public String activitiesView(Model model) {
        List<Activity> activities = activityDao.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("categories", activityCatDao.findAll());

//        List<String> states = activitiesStateLocation(activities);
//        List<String> cities = activitiesCityLocation(activities);
//        model.addAttribute("cities", cities);
//        model.addAttribute("states", states);
//        model.addAttribute("noLocation", true);
//        model.addAttribute("noCategory", true);
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication instanceof AnonymousAuthenticationToken)) {
//            User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            User user = userDAO.getUserById(loggedInUser.getId());
//            List<Activity> filteredList = new ArrayList<>();
//            for (int i = 0; i < activities.size(); i++) {
//                if (activities.get(i).getUser().getUserLocationState().equalsIgnoreCase(user.getUserLocationState())) {
//                    if (activities.get(i).getUser().getUserLocation().equalsIgnoreCase(user.getUserLocation())) {
//                        filteredList.add(activities.get(i));
//                    }
//                }
//            }
//            model.addAttribute("activities", filteredList);
//            model.addAttribute("user", user);
//            return "activities/activitiesView";
//        }

        model.addAttribute("noCategory", true);

        model.addAttribute("activities", activities);
        return "activities/activitiesView";
    }

    @GetMapping("/activities/{id}")
    public String activityView(Model model, @PathVariable Long id) {
        Activity activity = activityDao.getById(id);
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        activity.increaseViewCount();
        activityDao.save(activity);
        model.addAttribute("activity", activity);


        return "activities/activityView";
    }

    @GetMapping("/activities/categories/{id}")
    public String viewByCategory(Model model, @PathVariable Long id) {
        List<ActivityCategory> categories = activityCatDao.findAll();
        model.addAttribute("categories", activityCatDao.findAll());
        List<Activity> activities = activityCatDao.getById(id).getActivities();
        List<String> states = activitiesStateLocation(activities);
        List<String> cities = activitiesCityLocation(activities);
        model.addAttribute("cities", cities);
        model.addAttribute("states", states);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userDAO.getUserById(loggedInUser.getId());
            List<Activity> filteredList = new ArrayList<>();
            for (int i = 0; i < activities.size(); i++) {
                if (activities.get(i).getUser().getUserLocationState().equals(user.getUserLocationState())) {
                    if (activities.get(i).getUser().getUserLocation().equals(user.getUserLocation())) {
                        filteredList.add(activities.get(i));
                    }
                }
            }
            model.addAttribute("activities", filteredList);
            model.addAttribute("user", user);
            model.addAttribute("categories", categories);
            return "activities/activitiesView";
        }
        model.addAttribute("activities", activities);
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


        return "redirect:/activities";
    }


    @GetMapping("/activities/{id}/edit")
    public String editPost(@PathVariable Long id, Model model, RedirectAttributes attr) {
        Activity activity = activityDao.getById(id);
        List<ActivityCategory> categories = activityCatDao.findAll();
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userService.userVerification(loggedInUser.getId(), activity.getUser().getId())){
            model.addAttribute("filestackKey", filestackKey);
            model.addAttribute("activity", activity);
            model.addAttribute("categories", categories);

            return "activities/activityEdit";
        }
        attr.addFlashAttribute("alert", true);
        attr.addFlashAttribute("message", "You do not have permission to edit that content. Sign in to the correct account.");

        return "redirect:/login";
    }

    @PostMapping("/activities/edit")
    public String editAndSubmit(@ModelAttribute Activity activity) {


        activityDao.save(activity);
        return "redirect:/activities";
    }

    @GetMapping("/activities/{id}/delete")
    public String deleteActivity(@PathVariable Long id, Model model, RedirectAttributes attr) {
        Activity activity = activityDao.getById(id);
        activity.getActivityCategories().clear();
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userService.userVerification(loggedInUser.getId(), activity.getUser().getId())){
            activityDao.delete(activity);
            attr.addFlashAttribute("deleteMsg", "Successfully deleted the post");
            return "redirect:/activities";
        }
        attr.addFlashAttribute("alert", true);
        attr.addFlashAttribute("message", "You do not have permission to delete that content. Sign in to the correct account.");

        return "redirect:/login";
    }

    @GetMapping("/activities/user/{user_id}")
    public String seeAllUserPosts(@PathVariable Long user_id, Model model) {
        User targetUser = userDAO.getUserById(user_id);
        List<Activity> activities = activityDao.getByUser(targetUser);
        model.addAttribute("categories", activityCatDao.findAll());
        List<String> states = activitiesStateLocation(activityDao.findAll());
        List<String> cities = activitiesCityLocation(activityDao.findAll());
        model.addAttribute("cities", cities);
        model.addAttribute("states", states);
        model.addAttribute("activities", activities);
        return "activities/activityUserPosts";
    }


    //    checking for duplicate in List
    public static List<Activity> checkDuplicate(List<Activity> lists) {
        ArrayList<Activity> arr = new ArrayList<>();
        for (Activity element : lists) {
            if (!lists.contains(element)) {
                arr.add(element);
            }
        }
        return arr;
    }


    @GetMapping("/activities/search")
    public String filteredActivities(Model model, @RequestParam String keyword) {


        model.addAttribute("keyword", keyword.toLowerCase(Locale.ROOT));
        model.addAttribute("categories", activityCatDao.findAll());
        List<Activity> activities = activityDao.findAll();
        List<Activity> filteredActivities = new ArrayList<>();


        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            String title = activity.getTitle();
            String body = activity.getBody();


            if ((title.toLowerCase().contains(keyword.toLowerCase())) || (body.toLowerCase().contains(keyword.toLowerCase()))) {
                filteredActivities.add(activity); // wil have duplicates.
                checkDuplicate(filteredActivities);
            }
        }
        List<String> states = activitiesStateLocation(activities);
        List<String> cities = activitiesCityLocation(activities);
        model.addAttribute("cities", cities);
        model.addAttribute("states", states);
        model.addAttribute("activities", filteredActivities);
        model.addAttribute("noLocation", true);
        return "activities/ActivityFiltered";
    }

    @GetMapping("/admin/activities/{id}/delete")
    public String adminDeleteActivity(@PathVariable Long id, Model model, RedirectAttributes attr) {
        Activity activity = activityDao.getById(id);
        activity.getActivityCategories().clear();
        activityDao.delete(activity);
        attr.addFlashAttribute("deleteMsg", "Successfully deleted the post");
        return "redirect:/activities";
    }

    @GetMapping("/admin/activities/{id}/edit")
    public String adminEditPost(@PathVariable Long id, Model model, RedirectAttributes attr) {
        Activity activity = activityDao.getById(id);
        List<ActivityCategory> categories = activityCatDao.findAll();
        model.addAttribute("filestackKey", filestackKey);
        model.addAttribute("activity", activity);
        model.addAttribute("categories", categories);
        return "activities/activityEdit";
    }

    @GetMapping("/activities/filter")
    public String filterByStateAndCity(@RequestParam(required = false) String state, @RequestParam(required = false) String city, @RequestParam(required = false) String category,  Model model){
//        List<Activity> activities = activityDao.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<Activity> filteredList = activityDao.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("categories", activityCatDao.findAll());
        List<String> states = activitiesStateLocation(filteredList);
        List<String> cities = activitiesCityLocation(filteredList);
        model.addAttribute("cities", cities);
        model.addAttribute("states", states);

            if(state != null && city != null && category != null){
                List<Activity> activitiesByCat = activityCatDao.getActivityCategoryByName(category).getActivities();
                List<Activity> activitiesByState = filterByState(activitiesByCat, state);
                filteredList = filterByCity(activitiesByState, city);
            }else if(state != null && city != null){
                for (int i = 0; i < filteredList.size(); i++) {
                    if (filteredList.get(i).getUser().getUserLocationState().equals(state)) {
                        if (filteredList.get(i).getUser().getUserLocation().equals(city)) {
                            filteredList.add(filteredList.get(i));
                        }
                    }
                }
            }else if(state != null && category != null){
                List<Activity> activitiesByCat = activityCatDao.getActivityCategoryByName(category).getActivities();
                filteredList = filterByState(activitiesByCat, state);
            }else if(category != null && city != null){
                List<Activity> activitiesByCat = activityCatDao.getActivityCategoryByName(category).getActivities();
                filteredList = filterByCity(activitiesByCat, city);
            }else if(category != null && state == null && city == null) {
                ActivityCategory category1 = activityCatDao.getActivityCategoryByName(category);
                String redirect = "redirect:/activities/categories/" + category1.getId();
                return redirect;
            }else if(category == null && state != null && city == null){
                filteredList = filterByState(filteredList, state);
            }else if(category == null && state == null && city != null){
                filteredList = filterByCity(filteredList, city);
            }

            model.addAttribute("activities", filteredList);
            return "activities/activitiesView";
    }

    private List<String> activitiesStateLocation(List<Activity> activities){
        List<String> states = new ArrayList<>();
        for(int i = 0; i < activities.size(); i++){
            String stateLoop = activities.get(i).getUser().getUserLocationState();
            if(!states.contains(stateLoop)){
                states.add(stateLoop);
            }
        }
        return states;
    }

    private List<String> activitiesCityLocation(List<Activity> activities){
        List<String> cities = new ArrayList<>();
        for(int i = 0; i < activities.size(); i++){
            String cityLoop = WordUtils.capitalize(activities.get(i).getUser().getUserLocation());
            if(!cities.contains(cityLoop)){
                cities.add(cityLoop);
            }
        }
        return cities;
    }

    private List<Activity> filterByState(List<Activity> activities, String state){
        List<Activity> filtered = new ArrayList<>();
        for(int i = 0; i < activities.size(); i++){
            Activity activity = activities.get(i);
            if(activity.getUser().getUserLocationState().equalsIgnoreCase(state)){
                filtered.add(activity);
            }
        }
        return filtered;
    }

    private List<Activity> filterByCity(List<Activity> activities, String city){
        List<Activity> filtered = new ArrayList<>();
        for(int i = 0; i < activities.size(); i++){
            Activity activity = activities.get(i);
            if(activity.getUser().getUserLocation().equalsIgnoreCase(city)){
                filtered.add(activity);
            }
        }
        return filtered;
    }
}






