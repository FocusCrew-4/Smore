package com.smore.member.presentation.web.mapper;

import com.smore.member.application.service.command.CreateCommand;
import com.smore.member.application.service.command.FindCommand;
import com.smore.member.application.service.command.LoginCommand;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.presentation.web.dto.request.CreateRequestDto;
import com.smore.member.presentation.web.dto.request.LoginRequestDto;
import com.smore.member.presentation.web.dto.response.CreateResponseDto;
import com.smore.member.presentation.web.dto.response.FindResponseDto;
import org.springframework.stereotype.Component;

@Component
public class MemberControllerMapper {

    public LoginCommand toLoginCommand(LoginRequestDto requestDto) {
        return new LoginCommand(
            requestDto.email(),
            requestDto.password()
        );
    };

    public CreateCommand toCreateCommand(CreateRequestDto requestDto) {
        return new CreateCommand(
            requestDto.role(),
            requestDto.email(),
            requestDto.password(),
            requestDto.nickname()
        );
    }

    public CreateResponseDto toCreateResponseDto(MemberResult result) {
        return new CreateResponseDto(
            result.id(),
            result.email(),
            result.nickname()
        );
    }

    public FindCommand toFindCommand(Long requesterId, Long targetId) {
        return new FindCommand(requesterId, targetId);
    }

    public FindResponseDto toFindResponseDto(MemberResult findMember) {
        return new FindResponseDto(
            findMember.id(),
            findMember.role(),
            findMember.email(),
            findMember.nickname(),
            findMember.auctionCancelCount(),
            findMember.memberStatus(),
            findMember.deletedBy()
        );
    }
}
