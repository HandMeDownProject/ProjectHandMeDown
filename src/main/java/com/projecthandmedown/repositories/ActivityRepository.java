package com.projecthandmedown.repositories;
import com.projecthandmedown.models.Activity;
import com.projecthandmedown.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Activity getById(long id);
    List<Activity> getByUser(User user);

}
