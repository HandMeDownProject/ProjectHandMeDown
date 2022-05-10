package com.projecthandmedown.repositories;

import com.projecthandmedown.models.ForumPostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumCategoryRepository extends JpaRepository <ForumPostCategory, Long> {
    ForumPostCategory getById(long id);
}
