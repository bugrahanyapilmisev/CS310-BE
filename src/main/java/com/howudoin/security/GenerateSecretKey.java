package com.howudoin.security;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;

public class GenerateSecretKey {
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey = keyGen.generateKey();
        String base64EncodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Generated Key: " + base64EncodedKey);
    }
}
