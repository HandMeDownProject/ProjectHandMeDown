package com.projecthandmedown.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User getUserById(long id);
    User findByUsername(String username);
}

