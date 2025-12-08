package com.smore.member.application.port.in;

public interface MemberJwtVerify {

    boolean verify(String jwt);

}
