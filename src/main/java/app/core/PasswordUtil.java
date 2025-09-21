package app.core;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private static final int COST = 12; // 10-12 razonable para desktop demo

    public static String hash(String plain) {
        if (plain == null) throw new IllegalArgumentException("plain null");
        return BCrypt.hashpw(plain, BCrypt.gensalt(COST));
    }

    public static boolean verify(String plain, String hash) {
        if (plain == null || hash == null || hash.isBlank()) return false;
        try { return BCrypt.checkpw(plain, hash); }
        catch (Exception e) { return false; }
    }
}