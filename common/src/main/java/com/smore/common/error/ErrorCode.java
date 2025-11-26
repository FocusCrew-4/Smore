package com.smore.common.error;

public interface ErrorCode {
    String code();
    String message();
    String triggeredBy();
}
