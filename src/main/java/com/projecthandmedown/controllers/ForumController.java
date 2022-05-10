package com.projecthandmedown.controllers;
import com.projecthandmedown.models.*;
import com.projecthandmedown.repositories.ForumCategoryRepository;
import com.projecthandmedown.repositories.ForumPostRepository;
import com.projecthandmedown.repositories.ForumReplyRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ForumController {

    private final ForumPostRepository forumPostDao;
    private final ForumReplyRepository forumReplyDao;
    private final ForumCategoryRepository forumPostCategoryDao;
    private final UserRepository userDAO;
    private final EmailService emailService;

    public ForumController(ForumPostRepository forumPostDao, ForumReplyRepository forumReplyDao, ForumCategoryRepository forumPostCategoryDao, UserRepository userDAO, EmailService emailService) {
        this.forumPostDao = forumPostDao;
        this.forumReplyDao = forumReplyDao;
        this.forumPostCategoryDao = forumPostCategoryDao;
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
//        model.addAttribute("categories", categoriesDao.findAll());
        return "forums/forum";
    }

    @GetMapping("/forum_post/{id}")
//    @ResponseBody
    public String postID(@PathVariable long id, Model model) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        ForumPost topic = forumPostDao.getById(id); //split for categories
        List<ForumPostCategory> categories = topic.getForumPostCategories(); //split for categories
        model.addAttribute("posts", topic);
        model.addAttribute("categories", categories); //categories for individual post(topics)
        model.addAttribute("reply", new ForumReply());
        List<ForumReply> replies = forumReplyDao.getByForumPostId(id);
        model.addAttribute("replies", replies);
        return "forums/forumPostView";
    }

    //List<ForumPostCategory> categories = forumposts.

//    @GetMapping("/filter")
//    public String filterCategory(long id, Model model) {
//        ForumPost topic = forumPostDao.getById(id); //split for categories
//        List<ForumPostCategory> categories = topic.getForumPostCategories(); //split for categories
//        model.addAttribute("posts", categories);
//    }

    @GetMapping("/create/post")
    public String createPostingView(Model model){
        List<ForumPostCategory> categories = forumPostCategoryDao.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("post", new ForumPost());
//        model.addAttribute("filestackKey", filestackKey);
//        model.addAttribute("post", new ForumPost());
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
        return "redirect:/forum";
    }

    @PostMapping("/create/reply/{id}")
    public String addReply(@PathVariable Long id, @ModelAttribute ForumReply reply) {
        ForumReply comment = new ForumReply();
        comment.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        comment.setForumPost(forumPostDao.getById(id));
        comment.setBody(reply.getBody());
        forumReplyDao.save(comment);
        return "redirect:/forum_post/{id}";
    }

    @GetMapping("edit/reply/{id}")
//    @ResponseBody
    public String editReply(@PathVariable long id, Model model) {
        model.addAttribute("reply", forumReplyDao.getById(id));
        return "forums/forumEditReplyView";
    }

    @PostMapping("edit/reply")
//    @ResponseBody
    public String addEditedReply(@ModelAttribute ForumReply reply) {
        forumReplyDao.save(reply);
        String redirect = "redirect:/forum_post/" + reply.getForumPost().getId();
        return redirect;
    }

    @GetMapping("reply/{id}/delete")
    public String deleteComment(@PathVariable long id, Model model) {
        ForumReply reply = forumReplyDao.getById(id);
        forumReplyDao.delete(reply);
        return "redirect:/forum";
    }

    // right now I am trying to enable deleting a comment and redirecting to the post source after deletion.

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
