package com.smore.payment.shared.config;

public class UserContextHolder {
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public static void set(Long id) { userIdHolder.set(id); }
    public static Long get() { return userIdHolder.get(); }
    public static void clear() { userIdHolder.remove(); }
}
