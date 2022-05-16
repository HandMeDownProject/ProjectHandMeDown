package com.projecthandmedown.repositories;

import com.projecthandmedown.models.AdminDeletedEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminDeletedEmailRepository extends JpaRepository<AdminDeletedEmail, Long> {

    AdminDeletedEmail getAdminDeletedEmailByEmail(String email);
}
