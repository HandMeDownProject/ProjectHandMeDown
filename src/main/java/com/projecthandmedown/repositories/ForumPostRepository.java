package com.projecthandmedown.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<Post, Long> {

    Post getById(long id);
    List<Post> getByUser(User user);

}

