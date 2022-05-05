package com.projecthandmedown.controllers;
import com.projecthandmedown.models.Activity;
import com.projecthandmedown.models.Listing;
import com.projecthandmedown.models.User;
import com.projecthandmedown.repositories.ListingRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
public class ListingController {

    private final ListingRepository listingDao;
    private final UserRepository userDAO;
    private final EmailService emailService;


    public ListingController(ListingRepository listingDao, UserRepository userDAO, EmailService emailService) {
        this.listingDao = listingDao;
        this.emailService = emailService;
        this.userDAO = userDAO;
    }

//    public ListingController(ListingRepository listingDao, UserRepository userDAO) {
//        this.listingDao = listingDao;
//        this.userDAO = userDAO;
//    }

    @GetMapping("/listings")
//    @ResponseBody
    public String listings(Model model) {
        model.addAttribute("listings", listingDao.findAll());
        return "listings/listingsView";
    }

    @GetMapping("/listing/{id}")
    public String listingView(Model model, @PathVariable Long id){
        Listing listing = listingDao.getById(id);
        model.addAttribute("listing",listing);


        return "listings/listing";
    }

    @GetMapping("/create/listing")
    public String createListingView(Model model){
        model.addAttribute("listing", new Listing());
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

        listingDao.save(listing);

        return "redirect:/listings";
    }

    @GetMapping("/listing/edit/{id}")
    public String editListing(@PathVariable Long id, Model model){
        Listing listing = listingDao.getById(id);

        model.addAttribute("listing",listing);
        return "listings/listingEdit";

    }

    @PostMapping("listing/edit")
    public String editAndSubmitListing(@ModelAttribute Listing listing) {


        listingDao.save(listing);
        return "redirect:/listings";
    }
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
