package com.smore.member.presentation.web;

import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import com.smore.member.application.service.AuthService;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.usecase.MemberCreate;
import com.smore.member.domain.enums.Role;
import com.smore.member.presentation.web.dto.request.CreateRequestDto;
import com.smore.member.presentation.web.dto.request.LoginRequestDto;
import com.smore.member.presentation.web.dto.response.CreateResponseDto;
import com.smore.member.presentation.web.mapper.MemberControllerMapper;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberControllerMapper mapper;
    private final AuthService authService;
    private final MemberCreate memberCreate;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<?>> login(@RequestBody LoginRequestDto requestDto) {
        String token = authService.login(mapper.toLoginCommand(requestDto));
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .body(ApiResponse.ok(null));
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<?>> register(
        @RequestHeader("X-User-Role") Role role,
        @Valid @RequestBody CreateRequestDto requestDto
    ) {
        if (!role.equals(Role.NONE)) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("M401", "회원 가입은 비회원만 가능합니다"));
        }
        MemberResult member = memberCreate.createMember(mapper.toCreateCommand(requestDto));
        var res = mapper.toCreateResponseDto(member);

        URI uri = URI.create("/api/v1/members/" + res.id());

        return ResponseEntity.created(uri)
            .body(ApiResponse.ok(res));
    }

}
