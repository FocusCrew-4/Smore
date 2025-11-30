package com.smore.member.presentation.web;

import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import com.smore.member.application.service.RoleBasedMemberService;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.selector.MemberServiceSelector;
import com.smore.member.domain.enums.Role;
import com.smore.member.presentation.web.dto.request.CreateRequestDto;
import com.smore.member.presentation.web.dto.response.CreateResponseDto;
import com.smore.member.presentation.web.mapper.MemberControllerMapper;
import java.net.URI;
import lombok.RequiredArgsConstructor;
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

    private final MemberServiceSelector memberServiceSelector;
    private final MemberControllerMapper mapper;

    @PostMapping
    public ResponseEntity<CommonResponse<CreateResponseDto>> registerConsumer(
        @RequestHeader("X-User-Role") Role role,
        @RequestBody CreateRequestDto requestDto
    ) {
        RoleBasedMemberService service
            = memberServiceSelector.select(role);

        MemberResult member = service.readMember();
        var res = mapper.toCreateResponseDto(member);

        URI uri = URI.create("/api/v1/members/" + res.id());

        return ResponseEntity.created(uri)
            .body(ApiResponse.ok(res));
    }

}
