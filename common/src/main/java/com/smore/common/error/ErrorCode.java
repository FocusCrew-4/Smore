package com.smore.common.error;

import java.lang.StackWalker;

public interface ErrorCode {
    String code();
    String message();

    /** Resolve where this error was triggered. */
    default String triggeredBy() {
        return TriggerLocator.resolve();
    }

    /** StackWalker helper to find the closest caller frame. */
    final class TriggerLocator {
        private TriggerLocator() {}

        private static final StackWalker WALKER =
            StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

        private static final String UNKNOWN = "unknown";

        static String resolve() {
            return WALKER.walk(frames -> frames
                // Remove proxy/framework layers
                .filter(frame -> {
                    String className = frame.getClassName();
                    return !(className.startsWith("jdk.proxy")
                        || className.contains("$Proxy")
                        || className.contains("CGLIB$")
                        || className.startsWith("org.springframework"));
                })
                // Skip common error package itself
                .filter(frame -> !frame.getClassName().startsWith("com.smore.common.error"))
                // Only consider com.smore code
                .filter(frame -> frame.getClassName().startsWith("com.smore"))
                .findFirst()
                .map(frame -> frame.getClassName() + "#" + frame.getMethodName())
                .orElse(UNKNOWN)
            );
        }
    }
}
