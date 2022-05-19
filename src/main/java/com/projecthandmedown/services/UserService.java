package com.projecthandmedown.services;


import com.projecthandmedown.models.PasswordResetToken;
import com.projecthandmedown.models.User;
import com.projecthandmedown.repositories.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class UserService {

    private final PasswordResetTokenRepository passwordResetTokenDao;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    public UserService(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenDao = passwordResetTokenRepository;
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenDao.save(myToken);
    }

    public String validatePasswordResetToken(String token) {
        PasswordResetToken passToken = passwordResetTokenDao.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    public boolean userVerification(long userId, long postId){
        return userId == postId;
    }


}
