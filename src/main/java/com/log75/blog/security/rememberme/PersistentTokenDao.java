package com.log75.blog.security.rememberme;

/**
 * Created by alireza on 4/24/20.
 */

import com.log75.blog.model.PersistentLogin;
import com.log75.blog.repository.PersistentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional
public class PersistentTokenDao implements PersistentTokenRepository {

    private PersistentRepository persistentRepository;

    @Autowired
    public PersistentTokenDao(PersistentRepository persistentRepository) {
        this.persistentRepository = persistentRepository;
    }


    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        PersistentLogin logins = new PersistentLogin();
        logins.setUsername(token.getUsername());
        logins.setSeries(token.getSeries());
        logins.setToken(token.getTokenValue());
        logins.setLastUsed(token.getDate());
        persistentRepository.save(logins);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        PersistentLogin logins = persistentRepository.getOne(seriesId);

        if (logins != null) {
            return new PersistentRememberMeToken(logins.getUsername(),
                    logins.getSeries(), logins.getToken(), logins.getLastUsed());
        }

        return null;
    }

    @Override
    public void removeUserTokens(String username) {
        persistentRepository.deleteByUsername(username);
    }

    public void removeTokenBySeries(String series) {
       persistentRepository.deleteBySeries(series);
    }


    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {

        PersistentLogin logins = persistentRepository.findById(series).get();
        logins.setToken(tokenValue);
        logins.setLastUsed(lastUsed);
    }



}