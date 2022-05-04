package com.web.wit.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {
    public boolean hasUserId(Authentication authentication, String userId) {

        return authentication.getPrincipal().equals(userId);
    }
}
