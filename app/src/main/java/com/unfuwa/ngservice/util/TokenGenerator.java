package com.unfuwa.ngservice.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static String generateNewToken() {
        byte bytes [] = new byte[24];
        secureRandom.nextBytes(bytes);
        String token = base64Encoder.encodeToString(bytes);;

        return token;
    }
}
