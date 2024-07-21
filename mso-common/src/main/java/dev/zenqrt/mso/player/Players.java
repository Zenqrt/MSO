package dev.zenqrt.mso.player;

import java.util.Set;

public final class Players {

    private static final Set<String> ADMINS = Set.of("Walmqrt");
    private static final Set<String> EXCLUDED = Set.of("Walmqrt");

    private Players() {}

    public static boolean isAdmin(String username) {
        return ADMINS.contains(username);
    }

    public static boolean isExcluded(String username) {
        return EXCLUDED.contains(username);
    }

}
