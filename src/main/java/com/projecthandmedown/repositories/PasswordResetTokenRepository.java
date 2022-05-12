package com.projecthandmedown.repositories;

import com.projecthandmedown.models.PasswordResetToken;
import com.projecthandmedown.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.projecthandmedown.models.PasswordResetToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {


    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);

    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);
}

