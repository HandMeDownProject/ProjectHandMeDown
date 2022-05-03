package com.projecthandmedown.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="forum_post_category")
public class ForumPostCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "forumPostCategories")
    private List<ForumPost> forumPosts;

    public ForumPostCategory(long id, String name, List<ForumPost> forumPosts) {
        this.id = id;
        this.name = name;
        this.forumPosts = forumPosts;
    }

    public ForumPostCategory(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ForumPost> getForumPosts() {
        return forumPosts;
    }

    public void setForumPosts(List<ForumPost> forumPosts) {
        this.forumPosts = forumPosts;
    }
}
