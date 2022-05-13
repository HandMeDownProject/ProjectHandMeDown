package com.projecthandmedown.controllers;
import com.projecthandmedown.models.*;
import com.projecthandmedown.repositories.ForumCategoryRepository;
import com.projecthandmedown.repositories.ForumPostRepository;
import com.projecthandmedown.repositories.ForumReplyRepository;
import com.projecthandmedown.repositories.UserRepository;
import com.projecthandmedown.services.EmailService;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Timestamp;
import java.util.*;

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
        model.addAttribute("posts", forumPostDao.findAll(Sort.by(Sort.Direction.DESC, "id")));
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

    @GetMapping("/FilterByCategory/{cat_id}")
    public String forumPostsByCategory(@PathVariable Long cat_id, Model model) {
        List<ForumPost> posts = forumPostCategoryDao.getById(cat_id).getForumPosts();
        List<ForumPostCategory> cats = forumPostCategoryDao.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("cats", cats);
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


//    @GetMapping("activities/search")
//    public String filteredActivities(Model model, @RequestParam String keyword) {
//
//
//        model.addAttribute("keyword", keyword.toLowerCase(Locale.ROOT));
//        List<Activity> activities = activityDao.findAll();
//        List<Activity> filteredActivities = new ArrayList<>();
//
//
//
//        for (int i = 0; i < activities.size(); i++) {
//            Activity activity = activities.get(i);
//            String title = activity.getTitle();
//            String body = activity.getBody();
//
//
//            if((title.toLowerCase().contains(keyword.toLowerCase())) || (body.toLowerCase().contains(keyword.toLowerCase()))){
//                filteredActivities.add(activity); // wil have duplicates.
//                checkDuplicate(filteredActivities);
//            }
//        }
//
//
//
//        model.addAttribute("activities", filteredActivities);
//
//        return "activities/ActivityFiltered";
//    }

    @GetMapping("posts/search")
    public String findPosts(Model model, @RequestParam String keyword) {
        model.addAttribute("keyword", keyword.toLowerCase(Locale.ROOT));
        List<ForumPost> posts = forumPostDao.findAll();
        List<ForumPost> findKeywordPosts = new ArrayList<>();

        for (int i = 0; i < posts.size(); i++) {
            ForumPost post = posts.get(i);
            String title = post.getTitle();
            String body = post.getBody();


            if((title.toLowerCase().contains(keyword.toLowerCase())) || (body.toLowerCase().contains(keyword.toLowerCase()))){
                findKeywordPosts.add(post); // wil have duplicates.
                checkDuplicate(findKeywordPosts);
            }
        }

//        for (int i = 0; i < posts.size(); i++) {
//            ForumPost post = posts.get(i);
//            String title = post.getTitle();
//            String body = post.getBody();
//            if (title.toLowerCase().contains(keyword.toLowerCase())) {
//                findKeywordPosts.add(post);
//            }
//            if (body.toLowerCase().contains(keyword.toLowerCase())) {
//                findKeywordPosts.add(post);
//            }
//
//            for (int k = 0; k < findKeywordPosts.size(); k++) {
//                for (int j = 1; j < findKeywordPosts.size(); j++) {
//                    if (findKeywordPosts.get(k) == findKeywordPosts.get(j)) {
//                        findKeywordPosts.remove(j);
//                    }
//                }
//            }
//        }
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

    @GetMapping("edit/{id}/post")
//    @ResponseBody
    public String edit(@PathVariable long id, Model model) {
        model.addAttribute("post", forumPostDao.getById(id));
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
        post.getForumPostCategories().clear();
        forumPostDao.delete(post);
        return "redirect:/forum";
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

    //TODO: enable deleting a comment and redirecting to the post source after deletion.
}
