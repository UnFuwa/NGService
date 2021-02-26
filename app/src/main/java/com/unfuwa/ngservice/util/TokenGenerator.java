package com.unfuwa.ngservice.util;

import java.security.SecureRandom;

public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateNewToken() {
        byte bytes [] = new byte[24];
        secureRandom.nextBytes(bytes);
        String token = bytes.toString();

        return token;
    }
}
