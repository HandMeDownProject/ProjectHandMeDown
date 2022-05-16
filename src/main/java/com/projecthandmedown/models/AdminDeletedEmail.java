package com.projecthandmedown.models;

import javax.persistence.*;

@Entity
@Table(name = "admin_deleted_email")
public class AdminDeletedEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 150)
    private String email;

    public AdminDeletedEmail() {
    }

    public AdminDeletedEmail(long id, String email) {
        this.id = id;
        this.email = email;
    }

    public AdminDeletedEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
