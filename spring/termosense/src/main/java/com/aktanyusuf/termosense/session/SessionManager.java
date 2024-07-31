package com.aktanyusuf.termosense.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    private Map<String, String> sessionStore = new HashMap<>();

    public String createSession(String email) {
        String token = UUID.randomUUID().toString();
        sessionStore.put(token, email);
        return token;
    }

    public String getEmailByToken(String token) {
        return sessionStore.get(token);
    }

    public void invalidateSession(String token) {
        sessionStore.remove(token);
    }
}
