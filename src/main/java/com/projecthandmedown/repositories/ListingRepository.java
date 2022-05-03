package com.projecthandmedown.repositories;
import com.projecthandmedown.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    Post getById(long id);
    List<Post> getByUser(User user);

}

