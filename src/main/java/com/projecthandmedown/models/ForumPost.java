package com.projecthandmedown.models;
import org.hibernate.mapping.ToOne;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "forum_posts")
public class ForumPost implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false)
    private String body;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "forumPost")
    private List<ForumReply> forumReplies;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="forum_posts_categories",
            joinColumns={@JoinColumn(name="forum_post_id")},
            inverseJoinColumns={@JoinColumn(name="forum_category_id")}
    )
    private List<ForumPostCategory> forumPostCategories;

    public ForumPost() {
    }

    public ForumPost(long id, String title, String body, User user, List<ForumReply> forumReplies) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
        this.forumReplies = forumReplies;
    }

    public ForumPost(long id, String title, String body, User user, List<ForumReply> forumReplies, List<ForumPostCategory> forumPostCategories) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
        this.forumReplies = forumReplies;
        this.forumPostCategories = forumPostCategories;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ForumReply> getForumReplies() {
        return forumReplies;
    }

    public void setForumReplies(List<ForumReply> forumReplies) {
        this.forumReplies = forumReplies;
    }

    public List<ForumPostCategory> getForumPostCategories() {
        return forumPostCategories;
    }

    public void setForumPostCategories(List<ForumPostCategory> forumPostCategories) {
        this.forumPostCategories = forumPostCategories;
    }
}

