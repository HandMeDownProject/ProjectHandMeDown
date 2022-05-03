package com.projecthandmedown.models;
import org.hibernate.mapping.ToOne;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "listings")
public class Listing implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false)
    private String body;
    @Column(nullable = false)
    private String price;
    @Column(nullable = true)
    private String imageUrl;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="listings_categories",
            joinColumns={@JoinColumn(name="listing_id")},
            inverseJoinColumns={@JoinColumn(name="category_id")}
    )
    private List<ListingCategory> listingsCategories;

    public Listing() {
    }

    public Listing(long id, String title, String body, String price, User user) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.price = price;
        this.user = user;
    }

    public Listing(long id, String title, String body, String price, String imageUrl, User user) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.price = price;
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public Listing(long id, String title, String body, String price, String imageUrl, User user, List<ListingCategory> listingsCategories) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.price = price;
        this.imageUrl = imageUrl;
        this.user = user;
        this.listingsCategories = listingsCategories;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ListingCategory> getListingsCategories() {
        return listingsCategories;
    }

    public void setListingsCategories(List<ListingCategory> listingsCategories) {
        this.listingsCategories = listingsCategories;
    }
}

