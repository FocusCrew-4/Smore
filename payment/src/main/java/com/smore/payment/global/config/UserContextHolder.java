package com.smore.payment.global.config;

import java.util.UUID;

public class UserContextHolder {
    private static final ThreadLocal<UUID> userIdHolder = new ThreadLocal<>();

    public static void set(UUID id) { userIdHolder.set(id); }
    public static UUID get() { return userIdHolder.get(); }
    public static void clear() { userIdHolder.remove(); }
}
