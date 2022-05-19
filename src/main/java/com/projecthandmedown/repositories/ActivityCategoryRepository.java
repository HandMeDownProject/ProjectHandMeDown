package com.projecthandmedown.repositories;

import com.projecthandmedown.models.Activity;
import com.projecthandmedown.models.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory,Long> {

    ActivityCategory getById(Long id);

    List<ActivityCategory> getByActivities (Activity activity);

    ActivityCategory getActivityCategoryByName(String name);

}
