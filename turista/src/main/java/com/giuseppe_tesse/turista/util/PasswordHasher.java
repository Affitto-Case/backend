package com.giuseppe_tesse.turista.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHasher {

    private static final int COST = 12; // 10-14 è lo standard

    public static String hash(String plainPassword) {
        return BCrypt.withDefaults()
                .hashToString(COST, plainPassword.toCharArray());
    }

    public static boolean check(String plainPassword, String hashedPassword) {
        return BCrypt.verifyer()
                .verify(plainPassword.toCharArray(), hashedPassword)
                .verified;
    }
}
