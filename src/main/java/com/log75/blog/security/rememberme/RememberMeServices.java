package com.log75.blog.security.rememberme;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * Created by alireza on 4/24/20.
 */
public class RememberMeServices extends PersistentTokenBasedRememberMeServices {

    private PersistentTokenDao tokenRepository;
    public RememberMeServices(String key,
                              UserDetailsService userDetailsService,
                              PersistentTokenDao tokenRepository) {
        super(key, userDetailsService, tokenRepository);
        setParameter("remember-me");// parameter name in login form
        setTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(2000));
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        cancelCookie(request, response);
        if (authentication != null) {
            String rememberMeCookie = extractRememberMeCookie(request);
            if (rememberMeCookie != null && rememberMeCookie.length() != 0) {
                String[] cookieTokens = decodeCookie(rememberMeCookie);
                if (cookieTokens.length == 2) {
                    String series = cookieTokens[0];
                    //remove by series
                    tokenRepository.removeTokenBySeries(series);
                }
            }
        }
    }
}