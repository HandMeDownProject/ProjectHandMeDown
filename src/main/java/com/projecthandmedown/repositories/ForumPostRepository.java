package com.projecthandmedown.repositories;
import com.projecthandmedown.models.ForumPost;
import com.projecthandmedown.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    ForumPost getById(long id);
    List<ForumPost> getByUser(User user);

}

