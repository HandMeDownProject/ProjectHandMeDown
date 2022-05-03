package com.projecthandmedown.repositories;
import com.projecthandmedown.models.ForumReply;
import com.projecthandmedown.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ForumReplyRepository extends JpaRepository<ForumReply, Long> {

    ForumReply getById(long id);
    List<ForumReply> getByUser(User user);

}
