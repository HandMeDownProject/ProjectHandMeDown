package com.projecthandmedown.models;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String username;
    @Column(nullable = false, length = 100)
    private String email;
    @Column(nullable = false, length = 100)
    private String password;
    @Column(nullable = false)
    private String userLocation;
    @Column(nullable = false)
    private Boolean userIsAdmin;
    @Column(nullable = false, length = 20)
    private String userPhone;
    @Column(nullable = false, length = 100)
    private String userIMG;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<ForumPost> forumPosts;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Listing> listings;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<ForumReply> forumReplies;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Activity> activities;

    public User() {
    }

    public User(User copy) {
        id = copy.id; // This line is SUPER important! Many things won't work if it's absent
        email = copy.email;
        username = copy.username;
        password = copy.password;

    }

    public User(String username, String email, String password, String userLocation, String userPhone, String userIMG) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userLocation = userLocation;
        this.userPhone = userPhone;
        this.userIMG = userIMG;
    }


    public User(Long id, String username, String email, String password, String userLocation, Boolean userIsAdmin, String userPhone, String userIMG, List<ForumPost> forumPosts, List<Listing> listings, List<ForumReply> forumReplies, List<Activity> activities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.userLocation = userLocation;
        this.userIsAdmin = userIsAdmin;
        this.userPhone = userPhone;
        this.userIMG = userIMG;
        this.forumPosts = forumPosts;
        this.listings = listings;
        this.forumReplies = forumReplies;
        this.activities = activities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public Boolean getUserIsAdmin() {
        return userIsAdmin;
    }

    public void setUserIsAdmin(Boolean userIsAdmin) {
        this.userIsAdmin = userIsAdmin;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public List<ForumPost> getForumPosts() {
        return forumPosts;
    }

    public void setForumPosts(List<ForumPost> forumPosts) {
        this.forumPosts = forumPosts;
    }

    public List<Listing> getListings() {
        return listings;
    }

    public void setListings(List<Listing> listings) {
        this.listings = listings;
    }

    public List<ForumReply> getForumReplies() {
        return forumReplies;
    }

    public void setForumReplies(List<ForumReply> forumReplies) {
        this.forumReplies = forumReplies;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public String getUserIMG() {
        return userIMG;
    }

    public void setUserIMG(String userIMG) {
        this.userIMG = userIMG;
    }


}

