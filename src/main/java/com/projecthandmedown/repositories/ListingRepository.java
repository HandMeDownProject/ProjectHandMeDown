package com.projecthandmedown.repositories;
import com.projecthandmedown.models.Listing;
import com.projecthandmedown.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    Listing getById(long id);
    List<Listing> getByUser(User user);

}

