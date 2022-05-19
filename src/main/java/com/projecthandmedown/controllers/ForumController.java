package com.projecthandmedown.controllers;
import com.projecthandmedown.models.*;
import com.projecthandmedown.repositories.ForumCategoryRepository;
import com.projecthandmedown.repositories.ForumPostRepository;
import com.projecthandmedown.repositories.ForumReplyRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import com.projecthandmedown.services.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class ForumController {

    private final ForumPostRepository forumPostDao;
    private final ForumReplyRepository forumReplyDao;
    private final ForumCategoryRepository forumPostCategoryDao;
    private final UserRepository userDAO;
    private final EmailService emailService;
    private final UserService userService;
    public ForumController(ForumPostRepository forumPostDao, ForumReplyRepository forumReplyDao, ForumCategoryRepository forumPostCategoryDao, UserRepository userDAO, EmailService emailService, UserService userService) {
        this.forumPostDao = forumPostDao;
        this.forumReplyDao = forumReplyDao;
        this.forumPostCategoryDao = forumPostCategoryDao;
        this.emailService = emailService;
        this.userDAO = userDAO;
        this.userService = userService;
    }

    @GetMapping("/forum")
    public String posts(Model model) {
        model.addAttribute("posts", forumPostDao.findAll(Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("categories", forumPostCategoryDao.findAll());
        model.addAttribute("noCategory", true);
        return "forums/forum";
    }

    @GetMapping("/forum/reverse")
    public String postsReverse(Model model) {
        model.addAttribute("posts", forumPostDao.findAll());
        return "forums/forum";
    }

    @GetMapping("/forum_post/{id}")
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

    @GetMapping("/FilterByCategory/{cat_id}")
    public String forumPostsByCategory(@PathVariable Long cat_id, Model model) {
        List<ForumPost> posts = forumPostCategoryDao.getById(cat_id).getForumPosts();
        List<ForumPostCategory> cats = forumPostCategoryDao.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("categories", cats);
        model.addAttribute("categoryChosen", true);
        model.addAttribute("chosenCategory", forumPostCategoryDao.getById(cat_id));
        return "forums/forum";
    }

    public static List<ForumPost> checkDuplicate( List<ForumPost> posts){
        ArrayList<ForumPost> arr = new ArrayList<>();
        for (ForumPost element : posts){
            if(!posts.contains(element)){
                arr.add(element);
            }
        }
        return arr;
    }

    @GetMapping("/posts/search")
    public String findPosts(Model model, @RequestParam String keyword) {
        model.addAttribute("keyword", keyword.toLowerCase(Locale.ROOT));
        List<ForumPost> posts = forumPostDao.findAll();
        List<ForumPost> findKeywordPosts = new ArrayList<>();

        for (int i = 0; i < posts.size(); i++) {
            ForumPost post = posts.get(i);
            String title = post.getTitle();
            String body = post.getBody();


            if((title.toLowerCase().contains(keyword.toLowerCase())) || (body.toLowerCase().contains(keyword.toLowerCase()))){
                findKeywordPosts.add(post); // will hold duplicates.
                checkDuplicate(findKeywordPosts);
            }
        }

        model.addAttribute("posts", findKeywordPosts);
        return "forums/forum";
    }

    @GetMapping("/create/post")
    public String createPostingView(Model model){
        List<ForumPostCategory> categories = forumPostCategoryDao.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("post", new ForumPost());
        return "forums/createForumPost";
    }

    @PostMapping("/create/post")
    public String forumPostAdd(@ModelAttribute ForumPost post) {
        post.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String date = new String(String.valueOf(new Date(System.currentTimeMillis())));
        post.setTimestamp(date);
        System.out.println("timestamp = " + date);
        forumPostDao.save(post);
        return "redirect:/forum";
    }

    @GetMapping("/edit/{id}/post")
    public String edit(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        ForumPost post = forumPostDao.getById(id);
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userService.userVerification(loggedInUser.getId(), post.getUser().getId())){
            model.addAttribute("post", post);
            return "forums/forumEditPost";
        }
        redirectAttributes.addFlashAttribute("alert", true);
        redirectAttributes.addFlashAttribute("message", "You do not have permission to edit that content. Sign in to the correct account.");

        return "redirect:/login";
    }

    @PostMapping("/edit/post")
    public String edit(@ModelAttribute ForumPost post) {
        forumPostDao.save(post);
        return "redirect:/forum";
    }

    @GetMapping("/post/{id}/delete")
    public String delete(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        ForumPost post = forumPostDao.getById(id);
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userService.userVerification(loggedInUser.getId(), post.getUser().getId())){
            post.getForumPostCategories().clear();
            forumPostDao.delete(post);
            return "redirect:/forum";
        }
        redirectAttributes.addFlashAttribute("alert", true);
        redirectAttributes.addFlashAttribute("message", "You do not have permission to delete that content. Sign in to the correct account.");

        return "redirect:/login";
    }

    @PostMapping("/create/reply/{id}")
    public String addReply(@PathVariable Long id, @ModelAttribute ForumReply reply) {
        ForumReply comment = new ForumReply();
        comment.setUser((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        comment.setForumPost(forumPostDao.getById(id));
        comment.setBody(reply.getBody());
        String date = new String(String.valueOf(new Date(System.currentTimeMillis())));
        comment.setTimestamp(date);
        System.out.println("timestamp = " + date);
        forumReplyDao.save(comment);
        return "redirect:/forum_post/{id}";
    }

    @GetMapping("/edit/reply/{id}")
    public String editReply(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        ForumReply reply = forumReplyDao.getById(id);
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userService.userVerification(loggedInUser.getId(), reply.getUser().getId())){
            model.addAttribute("reply", reply);
            return "forums/forumEditReplyView";
        }
        redirectAttributes.addFlashAttribute("alert", true);
        redirectAttributes.addFlashAttribute("message", "You do not have permission to delete that content. Sign in to the correct account.");

        return "redirect:/login";
    }

    @PostMapping("/edit/reply")
    public String addEditedReply(@ModelAttribute ForumReply reply) {
        forumReplyDao.save(reply);
        String redirect = "redirect:/forum_post/" + reply.getForumPost().getId();
        return redirect;
    }

    @GetMapping("/reply/{id}/delete")
    public String deleteComment(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        ForumReply reply = forumReplyDao.getById(id);
        ForumPost post = reply.getForumPost();
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userService.userVerification(loggedInUser.getId(), reply.getUser().getId())) {
            forumReplyDao.delete(reply);

            return "redirect:/forum_post/" + post.getId();
        }
        redirectAttributes.addFlashAttribute("alert", true);
        redirectAttributes.addFlashAttribute("message", "You do not have permission to delete that content. Sign in to the correct account.");

        return "redirect:/login";
    }

    @GetMapping("/admin/edit/{id}/post")
    public String adminEditPost(@PathVariable long id, Model model){
        ForumPost post = forumPostDao.getById(id);
        model.addAttribute("post", post);
        return "forums/forumEditPost";
    }

    @GetMapping("/admin/post/{id}/delete")
    public String adminDelete(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        ForumPost post = forumPostDao.getById(id);
        post.getForumPostCategories().clear();
        forumPostDao.delete(post);
        return "redirect:/forum";
    }

    @GetMapping("/admin/reply/{id}/delete")
    public String adminDeleteComment(@PathVariable long id, Model model) {
        ForumReply reply = forumReplyDao.getById(id);
        ForumPost post = reply.getForumPost();
        forumReplyDao.delete(reply);
        return "redirect:/forum_post/" + post.getId();
    }

    @GetMapping("/admin/edit/reply/{id}")
    public String adminEditReply(@PathVariable long id, Model model) {
        model.addAttribute("reply", forumReplyDao.getById(id));
        return "forums/forumEditReplyView";
    }
    //TODO: enable deleting a comment and redirecting to the post source after deletion.
}
