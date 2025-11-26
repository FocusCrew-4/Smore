package com.smore.common.error;

import java.lang.StackWalker.Option;

public enum GlobalErrorCode implements ErrorCode {
    INVALID_INPUT("C001", "Invalid input"),
    INTERNAL_ERROR("C002", "Internal server error");

    private final String code;
    private final String message;
    private final String triggeredBy;
    GlobalErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
        this.triggeredBy = StackWalker
            .getInstance(Option.RETAIN_CLASS_REFERENCE)
            .walk(frames -> frames
                // 1. 프록시 프레임 제거
                .filter(frame -> {
                    String c = frame.getClassName();
                    return !(c.startsWith("jdk.proxy")
                        || c.contains("$Proxy")
                        || c.contains("CGLIB$")
                        || c.startsWith("org.springframework"));
                })
                // 2. 자기 자신(에러 정의 영역) 제거
                .filter(frame -> !frame.getClassName().startsWith("com.smore.common.error"))
                // 3. com.smore 아래의 진짜 호출자만 선택
                .filter(frame -> frame.getClassName().startsWith("com.smore"))
                .findFirst()
                .map(frame -> frame.getClassName() + "#" + frame.getMethodName())
                .orElse("unknown")
            );
    }

    @Override
    public String code() { return code; }

    @Override
    public String message() { return message; }

    @Override
    public String triggeredBy() { return triggeredBy; }


}
