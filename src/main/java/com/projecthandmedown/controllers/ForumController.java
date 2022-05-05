package com.projecthandmedown.controllers;
import com.projecthandmedown.models.ForumPost;
import com.projecthandmedown.models.ForumReply;
import com.projecthandmedown.models.Listing;
import com.projecthandmedown.models.User;
import com.projecthandmedown.repositories.ForumPostRepository;
import com.projecthandmedown.repositories.ForumReplyRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ForumController {

    private final ForumPostRepository forumPostDao;
    private final ForumReplyRepository forumReplyDao;
    private final UserRepository userDAO;
    private final EmailService emailService;

    public ForumController(ForumPostRepository forumPostDao, ForumReplyRepository forumReplyDao, UserRepository userDAO, EmailService emailService) {
        this.forumPostDao = forumPostDao;
        this.forumReplyDao = forumReplyDao;
        this.emailService = emailService;
        this.userDAO = userDAO;
    }

//    public ForumController(ForumPostRepository forumPostDao, UserRepository userDAO, ForumReplyRepository forumReplyDao, EmailService emailService) {
//        this.forumPostDao = forumPostDao;
//        this.forumReplyDao = forumReplyDao;
//        this.emailService = emailService;
//        this.userDAO = userDAO;
//    }

//    public PostController(PostRepository postDao, UserRepository userDAO) {
//        this.postDao = postDao;
//        this.userDAO = userDAO;
//    }

    @GetMapping("/forum")
//    @ResponseBody
    public String posts(Model model) {
        model.addAttribute("posts", forumPostDao.findAll());
        return "forums/forum";
    }

    @GetMapping("/forum_post/{id}")
//    @ResponseBody
    public String postID(@PathVariable long id, Model model) {
        model.addAttribute("posts", forumPostDao.getById(id)); //findAllById(Collections.singleton(id)));
        ForumReply replies = forumReplyDao.getById(id);
        model.addAttribute("replies", forumReplyDao.findAll());
        return "forums/forumPostView";
    }

    @GetMapping("/create/post")
    public String createPostingView(Model model){
        model.addAttribute("post", new ForumPost());
//        listing.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        if(listing.getTitle().equals("") || listing.getBody().equals("")){
//            return "listings/listingsView";
//        }
//        listingDao.save(listing);
//        emailService.prepareAndSend(listing, "listing created", "Confirmation: your listing has been created");
        return "forums/createForumPost";
    }

    @PostMapping("/create/post")
    public String forumPostAdd(@ModelAttribute ForumPost post) {
        post.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        forumPostDao.save(post);
        return "redirect:/forum";
    }

    @GetMapping("edit/{id}/post")
//    @ResponseBody
    public String edit(@PathVariable long id, Model model) {
        model.addAttribute("post", forumPostDao.getById(id));
//        model.addAttribute("user", new User());
        return "forums/forumEditPost";
    }

    @PostMapping("/edit/post")
    public String edit(@ModelAttribute ForumPost post) {
        forumPostDao.save(post);
        return "redirect:/forum";
    }

    @GetMapping("post/{id}/delete")
    public String delete(@PathVariable long id, Model model) {
        ForumPost post = forumPostDao.getById(id);
        forumPostDao.delete(post);
        return "redirect:/";
    }
//
//    @PostMapping("/edit/post")
//    public String edit(@ModelAttribute ForumReply reply) {
//        forumReplyDao.save(reply);
//        return "/forum/forumPostView";
//    }

//    @GetMapping("/create/post")
////    @ResponseBody
//    public String create(Model model) {
//        model.addAttribute("post", new ForumPost());
////        model.addAttribute("user", new User());
//        return "forums/createForumPost";
//    }
//
//    @PostMapping("/create/post")
//    public String post(@ModelAttribute ForumPost post) {
//        post.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
////        if(post.getTitle().equals("") || post.getBody().equals("")){
////            return "forum/createForumPost";
////        }
//        forumPostDao.save(post);
////        emailService.prepareAndSend(post, "post created", "Confirmation: your post has been created");
//        return "redirect:/forum";
//    }

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
//    @GetMapping("/error")
//    public String error(){
//        return "/error/500.html";
//    }


}

