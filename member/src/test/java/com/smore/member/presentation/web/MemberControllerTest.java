package com.smore.member.presentation.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.member.application.service.AuthService;
import com.smore.member.application.service.command.CreateCommand;
import com.smore.member.application.service.command.FindCommand;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.selector.MemberFindSelector;
import com.smore.member.application.service.selector.MemberInfoUpdateSelector;
import com.smore.member.application.service.usecase.MemberCreate;
import com.smore.member.application.service.usecase.MemberFind;
import com.smore.member.domain.enums.MemberStatus;
import com.smore.member.domain.enums.Role;
import com.smore.member.presentation.web.dto.request.CreateRequestDto;
import com.smore.member.presentation.web.dto.response.CreateResponseDto;
import com.smore.member.presentation.web.dto.response.FindResponseDto;
import com.smore.member.presentation.web.mapper.MemberControllerMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MemberCreate memberCreate;

    @MockitoBean
    MemberControllerMapper memberControllerMapper;

    @MockitoBean
    MemberFindSelector memberFindSelector;

    @MockitoBean
    MemberInfoUpdateSelector memberInfoUpdateSelector;

    @MockitoBean
    AuthService authService;

    @Test
    @DisplayName("POST /api/v1/members - 201 Created with response body")
    void registerConsumer() throws Exception {
        // given
        CreateRequestDto requestDto = new CreateRequestDto(
            Role.CONSUMER,
            "user@example.com",
            "password123!",
            "nickname"
        );

        MemberResult memberResult = new MemberResult(
            1L,
            Role.CONSUMER,
            requestDto.email(),
            requestDto.nickname(),
            0,
            MemberStatus.ACTIVE,
            null,
            null,
            null,
            null
        );

        CreateResponseDto responseDto = new CreateResponseDto(
            memberResult.id(),
            memberResult.email(),
            memberResult.nickname()
        );

        CreateCommand command = new CreateCommand(
            requestDto.role(),
            requestDto.email(),
            requestDto.password(),
            requestDto.nickname()
        );

        when(memberControllerMapper.toCreateCommand(requestDto)).thenReturn(command);
        when(memberCreate.createMember(command)).thenReturn(memberResult);
        when(memberControllerMapper.toCreateResponseDto(memberResult)).thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/v1/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Role", Role.NONE)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/v1/members/" + responseDto.id()))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(responseDto.id()))
            .andExpect(jsonPath("$.data.email").value(responseDto.email()))
            .andExpect(jsonPath("$.data.nickname").value(responseDto.nickname()))
            .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Nested
    @DisplayName("GET /api/v1/members/me")
    class GetMemberMe {
        @Test
        @DisplayName("USER 역할이 자신의 정보를 조회한다")
        void getOwnProfileAsUser() throws Exception {
            // given
            Long userId = 1L;
            Role role = Role.USER;

            MemberResult memberResult = new MemberResult(
                userId,
                role,
                "user@example.com",
                "user",
                0,
                MemberStatus.ACTIVE,
                null,
                null,
                null,
                null
            );

        FindResponseDto responseDto = new FindResponseDto(
            memberResult.id(),
            memberResult.role(),
            memberResult.email(),
            memberResult.nickname(),
            memberResult.auctionCancelCount(),
            memberResult.memberStatus(),
            memberResult.deletedBy()
        );

            FindCommand findCommand = new FindCommand(userId, userId);
            MemberFind memberFind = mock(MemberFind.class);

            when(memberFindSelector.select(role)).thenReturn(memberFind);
            when(memberControllerMapper.toFindCommand(userId, userId)).thenReturn(findCommand);
            when(memberFind.findMember(findCommand)).thenReturn(memberResult);
            when(memberControllerMapper.toFindResponseDto(memberResult)).thenReturn(responseDto);

            // when & then
            mockMvc.perform(get("/api/v1/members/me")
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(responseDto.id()))
                .andExpect(jsonPath("$.data.email").value(responseDto.email()))
                .andExpect(jsonPath("$.data.nickname").value(responseDto.nickname()))
                .andExpect(jsonPath("$.data.role").value(responseDto.role().name()))
                .andExpect(jsonPath("$.data.memberStatus").value(responseDto.memberStatus().name()))
                .andExpect(jsonPath("$.data.auctionCancelCount").value(responseDto.auctionCancelCount()))
                .andExpect(jsonPath("$.error").doesNotExist());

            verify(memberFindSelector).select(role);
            verify(memberFind).findMember(findCommand);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/members/{id}")
    class GetMemberById {
        @Test
        @DisplayName("ADMIN 역할이 다른 회원을 조회한다")
        void adminReadsOtherMember() throws Exception {
            // given
            Long adminId = 99L;
            Long targetId = 2L;
            Role role = Role.ADMIN;

            MemberResult memberResult = new MemberResult(
                targetId,
                Role.CONSUMER,
                "target@example.com",
                "target",
                1,
                MemberStatus.ACTIVE,
                null,
                null,
                null,
                null
            );

        FindResponseDto responseDto = new FindResponseDto(
            memberResult.id(),
            memberResult.role(),
            memberResult.email(),
            memberResult.nickname(),
            memberResult.auctionCancelCount(),
            memberResult.memberStatus(),
            memberResult.deletedBy()
        );

            FindCommand findCommand = new FindCommand(adminId, targetId);
            MemberFind memberFind = mock(MemberFind.class);

            when(memberFindSelector.select(role)).thenReturn(memberFind);
            when(memberControllerMapper.toFindCommand(adminId, targetId)).thenReturn(findCommand);
            when(memberFind.findMember(findCommand)).thenReturn(memberResult);
            when(memberControllerMapper.toFindResponseDto(memberResult)).thenReturn(responseDto);

            // when & then
            mockMvc.perform(get("/api/v1/members/{id}", targetId)
                    .header("X-User-Id", adminId)
                    .header("X-User-Role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(responseDto.id()))
                .andExpect(jsonPath("$.data.email").value(responseDto.email()))
                .andExpect(jsonPath("$.data.nickname").value(responseDto.nickname()))
                .andExpect(jsonPath("$.data.role").value(responseDto.role().name()))
                .andExpect(jsonPath("$.data.memberStatus").value(responseDto.memberStatus().name()))
                .andExpect(jsonPath("$.data.auctionCancelCount").value(responseDto.auctionCancelCount()))
                .andExpect(jsonPath("$.error").doesNotExist());

            verify(memberFindSelector).select(role);
            verify(memberFind).findMember(findCommand);
        }
    }
}
