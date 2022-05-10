package com.projecthandmedown.repositories;
import com.projecthandmedown.models.Listing;
import com.projecthandmedown.models.ListingCategory;
import com.projecthandmedown.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingCategoryRepository extends JpaRepository<ListingCategory, Long> {

    ListingCategory getById(long id);
//    List<Listing> getByUser(User user);


}

