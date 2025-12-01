package com.smore.member.presentation.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.member.application.service.AuthService;
import com.smore.member.application.service.command.CreateCommand;
import com.smore.member.application.service.result.MemberResult;
import com.smore.member.application.service.selector.MemberFindSelector;
import com.smore.member.application.service.usecase.MemberCreate;
import com.smore.member.domain.enums.MemberStatus;
import com.smore.member.domain.enums.Role;
import com.smore.member.presentation.web.dto.request.CreateRequestDto;
import com.smore.member.presentation.web.dto.response.CreateResponseDto;
import com.smore.member.presentation.web.mapper.MemberControllerMapper;
import org.junit.jupiter.api.DisplayName;
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
}
