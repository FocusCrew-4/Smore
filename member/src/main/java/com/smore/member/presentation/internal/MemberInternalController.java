package com.smore.member.presentation.internal;

import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import com.smore.common.response.ErrorResponse;
import com.smore.member.application.port.in.MemberJwtVerify;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/internal/members")
@RestController
@RequiredArgsConstructor
public class MemberInternalController {

    private final MemberJwtVerify memberJwtVerify;

    // TODO: 에러타입 맞추기 필요
    @GetMapping()
    public Boolean verifyJwtMember(
        @RequestHeader("Authorization") String jwtToken
    ) {
        boolean isOk = memberJwtVerify.verify(jwtToken);

//        if (!isOk) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                .body(ApiResponse.error("M401", "jwt 정보와 회원 정보가 일치하지 않습니다"));
//        }
//        return ResponseEntity.ok(ApiResponse.ok(isOk));

        return isOk;
    }
}
