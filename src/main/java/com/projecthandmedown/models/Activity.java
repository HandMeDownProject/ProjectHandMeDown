package com.projecthandmedown.models;
import org.hibernate.mapping.ToOne;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "activities")
public class Activity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false)
    private String body;
    @Column(nullable = true)
    private String imageUrl;
    @Column(nullable = true)
    private String webUrl;
    @Column(nullable = true)
    private String timestamp;
    @Column(nullable = false)
    private Long viewCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="activities_categories",
            joinColumns={@JoinColumn(name="activity_id")},
            inverseJoinColumns={@JoinColumn(name="activity_category_id")}
    )
    private List<ActivityCategory> activityCategories;

    public Activity() {
    }

    public Activity(long id, String title, String body, User user, List<ActivityCategory> activityCategories, String timestamp, Long viewCount ) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
        this.activityCategories = activityCategories;
        this.timestamp = timestamp;
        this.viewCount = viewCount;
    }

    public Activity(long id, String title, String body, String webUrl, User user, List<ActivityCategory> activityCategories, String timestamp, Long viewCount ) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.webUrl = webUrl;
        this.user = user;
        this.activityCategories = activityCategories;
        this.timestamp = timestamp;
        this.viewCount = viewCount;

    }

    public Activity(long id, String title, String body, String imageUrl, String webUrl, User user, List<ActivityCategory> activityCategories, Long viewCount ) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.webUrl = webUrl;
        this.user = user;
        this.activityCategories = activityCategories;
        this.viewCount = viewCount;

    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ActivityCategory> getActivityCategories() {
        return activityCategories;
    }

    public void setActivityCategories(List<ActivityCategory> activityCategories) {
        this.activityCategories = activityCategories;
    }
    public void increaseViewCount(){
       this.viewCount += 1;
    }
}

