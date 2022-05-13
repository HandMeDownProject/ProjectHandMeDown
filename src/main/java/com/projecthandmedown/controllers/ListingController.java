package com.projecthandmedown.controllers;

import com.projecthandmedown.models.Activity;
import com.projecthandmedown.models.Listing;
import com.projecthandmedown.models.ListingCategory;
import com.projecthandmedown.models.User;
import com.projecthandmedown.repositories.ListingCategoryRepository;
import com.projecthandmedown.repositories.ListingRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContext;

import org.springframework.http.converter.json.GsonBuilderUtils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Timestamp;
import java.util.*;

@Controller
public class ListingController {

    private final ListingRepository listingDao;
    private final UserRepository userDAO;
    private final EmailService emailService;
    private final ListingCategoryRepository listingCategoryDao;


    public ListingController(ListingRepository listingDao, UserRepository userDAO, EmailService emailService, ListingCategoryRepository listingCategoryDao) {
        this.listingDao = listingDao;
        this.emailService = emailService;
        this.userDAO = userDAO;
        this.listingCategoryDao = listingCategoryDao;
    }

//    public ListingController(ListingRepository listingDao, UserRepository userDAO) {
//        this.listingDao = listingDao;
//        this.userDAO = userDAO;
//    }

    @GetMapping("/listings")
//    @ResponseBody
    public String listings(Model model) {
        model.addAttribute("listings", listingDao.findAll(Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("cats", listingCategoryDao.findAll());
        return "listings/listingsView";
    }

    @GetMapping("/listings/reverse")
//    @ResponseBody
    public String listingsReverse(Model model) {
        model.addAttribute("listings", listingDao.findAll());
        model.addAttribute("cats", listingCategoryDao.findAll());
        return "listings/listingsView";
    }


    @GetMapping("/listing/{id}")
    public String listingView(Model model, @PathVariable Long id) {
        Listing listing = listingDao.getById(id);
        List<ListingCategory> cats = listing.getListingsCategories();
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        model.addAttribute("listing", listing);
        model.addAttribute("cats", cats);


        return "listings/listing";
    }

    @Value("${filestack.api.key}")
    private String filestackKey;

    @GetMapping("/create/listing")
    public String createListingView(Model model) {
        List<ListingCategory> cats = (List<ListingCategory>) listingCategoryDao.findAll();
        model.addAttribute("cats", cats);
        model.addAttribute("listing", new Listing());
        model.addAttribute("filestackKey", filestackKey);
//        listing.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        if(listing.getTitle().equals("") || listing.getBody().equals("")){
//            return "listings/listingsView";
//        }
//        listingDao.save(listing);
//        emailService.prepareAndSend(listing, "listing created", "Confirmation: your listing has been created");

        return "listings/createListing";
    }

    @PostMapping("/create/listing")
    public String listingsAdd(@ModelAttribute Listing listing) {
        listing.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String date = String.valueOf(new Date(System.currentTimeMillis()));
        listing.setTimestamp(date);
//        System.out.println("timestamp = " + date);

        listingDao.save(listing);


        return "redirect:/listings";
    }

    @GetMapping("/listing/edit/{id}")
    public String editListing(@PathVariable Long id, Model model) {
        Listing listing = listingDao.getById(id);
        List<ListingCategory> cats = listing.getListingsCategories();
        List<ListingCategory> allCats = listingCategoryDao.findAll();
        model.addAttribute("allCats", allCats);
        model.addAttribute("cats", cats);
        model.addAttribute("listing", listing);
        return "listings/listingEdit";

    }

    @PostMapping("listing/edit")
    public String editAndSubmitListing(@ModelAttribute Listing listing) {


        listingDao.save(listing);
        return "redirect:/listings";
    }

    @PostMapping("listing/delete/{id}")
    public String deleteListing(@PathVariable Long id) {
        Listing listing = listingDao.getById(id);
        listing.getListingsCategories().clear();

        listingDao.delete(listing);
        return "redirect:/listings";
    }

    @GetMapping("listings/user/{user_id}")
    public String seeAllUserListings(@PathVariable Long user_id, Model model) {
        User targetUser = userDAO.getUserById(user_id);
        List<Listing> listings = listingDao.getByUser(targetUser);
        model.addAttribute("listings", listings);
        model.addAttribute("user", targetUser);

        return "listings/listingUserPosts";
    }

    @GetMapping("listingsByCat/{cat_id}")
    public String listingsByCat(@PathVariable Long cat_id, Model model) {
        List<Listing> listings = listingCategoryDao.getById(cat_id).getListings();
        List<ListingCategory> cats = listingCategoryDao.findAll();
        model.addAttribute("listings", listings);
        model.addAttribute("cats", cats);

        return "listings/listingsView";
    }

    @GetMapping("listings/search")
    public String filteredActivities(Model model, @RequestParam String keyword) {
        model.addAttribute("keyword", keyword.toLowerCase(Locale.ROOT));
        List<Listing> listings = listingDao.findAll();
        List<Listing> filteredListings = new ArrayList<>();

        for (int i = 0; i < listings.size(); i++) {
            Listing listing = listings.get(i);
            String title = listing.getTitle();
            String body = listing.getBody();
            if (title.toLowerCase().contains(keyword.toLowerCase())) {
                filteredListings.add(listing);
            }
            if (body.toLowerCase().contains(keyword.toLowerCase())) {
                filteredListings.add(listing);
            }

            for (int k = 0; k < filteredListings.size(); k++) {
                for (int j = 1; j < filteredListings.size(); j++) {
                    if (filteredListings.get(k) == filteredListings.get(j)) {
                        filteredListings.remove(j);
                    }
                }
            }
        }
        model.addAttribute("listings", filteredListings);
        return "listings/listingsView";
    }

//    /listingsByCat/{cat_id}


//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        activity.setUser(user); // <-- this will be setting     user for post.
//
//        if(activity.getUser().equals(user) ) {
//            return "redirect:/activities";
//        }
//

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