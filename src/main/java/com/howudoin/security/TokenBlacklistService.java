package com.howudoin.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void addToBlacklist(String token, long expirationTime) {
        blacklist.put(token, expirationTime);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }


    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }

}
