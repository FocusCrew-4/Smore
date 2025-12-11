package com.smore.member.presentation.web;

import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import com.smore.member.application.service.AuthService;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.selector.MemberDeleteSelector;
import com.smore.member.application.service.selector.MemberFindSelector;
import com.smore.member.application.service.selector.MemberInfoUpdateSelector;
import com.smore.member.application.service.usecase.MemberCreate;
import com.smore.member.domain.enums.Role;
import com.smore.member.presentation.web.dto.request.CreateRequestDto;
import com.smore.member.presentation.web.dto.request.LoginRequestDto;
import com.smore.member.presentation.web.dto.request.UpdateInfoRequestDto;
import com.smore.member.presentation.web.mapper.MemberControllerMapper;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final MemberFindSelector memberFindSelector;
    private final MemberInfoUpdateSelector memberInfoUpdateSelector;
    private final MemberDeleteSelector memberDeleteSelector;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<String>> login(@RequestBody LoginRequestDto requestDto) {
        String token = authService.login(mapper.toLoginCommand(requestDto));
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .body(ApiResponse.ok("로그인 성공"));
    }

    // TODO: SELLER 회원가입 요청시 사용대기 상태로 두고 Member 에 이벤트 발행하여 수락하면 ACTIVE 되는 코드 구현 필요
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<?>> register(
        @RequestHeader("X-User-Role") Role role,
        @Valid @RequestBody CreateRequestDto requestDto
    ) {
        if (!role.isNONE()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("M401", "회원 가입은 비회원만 가능합니다"));
        }
        MemberResult member = memberCreate.createMember(mapper.toCreateCommand(requestDto));
        var res = mapper.toCreateResponseDto(member);

        URI uri = URI.create("/api/v1/members/" + res.id());

        return ResponseEntity.created(uri)
            .body(ApiResponse.ok(res));
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<?>> getMember(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") Role role
    ) {
        MemberResult findMember
            = memberFindSelector.select(role).findMember(mapper.toFindCommand(requesterId, requesterId));
        var res = mapper.toFindResponseDto(findMember);

        return  ResponseEntity.ok(
            ApiResponse.ok(res)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<?>> getMemberById(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") Role role,
        @PathVariable("id") Long targetId
    ) {
        MemberResult findMember
            = memberFindSelector.select(role).findMember(mapper.toFindCommand(requesterId, targetId));
        var res = mapper.toFindResponseDto(findMember);

        return  ResponseEntity.ok(
            ApiResponse.ok(res)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommonResponse<String>> updateMember(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") Role role,
        @PathVariable("id") Long targetId,
        @RequestBody UpdateInfoRequestDto requestDto
    ) {
        MemberResult updateMember
            = memberInfoUpdateSelector.select(role)
            .update(mapper.toUpdateInfoCommand(requesterId, targetId, requestDto));

        return ResponseEntity.ok(ApiResponse.ok("정보 수정 성공"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<String>> deleteMember(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") Role role,
        @PathVariable("id") Long targetId
    ) {
        MemberResult deletedMember
            = memberDeleteSelector.select(role)
            .delete(mapper.toDeleteCommand(requesterId, targetId));

        return ResponseEntity.noContent()
            .build();
    }
}
