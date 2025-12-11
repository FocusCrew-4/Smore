package com.smore.payment.global.config;

import java.util.UUID;

public class UserContextHolder {
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public static void set(Long id) { userIdHolder.set(id); }
    public static Long get() { return userIdHolder.get(); }
    public static void clear() { userIdHolder.remove(); }
}
