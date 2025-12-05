package com.smore.seller.presentation.web;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.seller.application.command.ApplyCommand;
import com.smore.seller.application.result.SellerResult;
import com.smore.seller.application.usecase.SellerApply;
import com.smore.seller.application.usecase.SellerApprove;
import com.smore.seller.application.usecase.SellerReject;
import com.smore.seller.domain.enums.SellerStatus;
import com.smore.seller.presentation.web.dto.request.SellerApplyRequestDto;
import com.smore.seller.presentation.web.mapper.SellerControllerMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SellerController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class SellerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SellerApply sellerApply;

    @MockitoBean
    SellerReject sellerReject;

    @MockitoBean
    SellerControllerMapper mapper;

    @MockitoBean
    SellerApprove sellerApprove;

    @Nested
    @DisplayName("POST /api/v1/sellers")
    class ApplySeller {

        @Test
        @DisplayName("POST /api/v1/sellers - CONSUMER가 아니면 403")
        void applySeller_forbiddenWhenRoleIsNotConsumer() throws Exception {
            SellerApplyRequestDto requestDto = new SellerApplyRequestDto("123-45-67890");

            mockMvc.perform(post("/api/v1/sellers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-User-Id", 1L)
                    .header("X-User-Role", "ADMIN")
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value("S401"))
                .andExpect(jsonPath("$.error.message").value("일반회원만 이용 가능합니다"));

            verifyNoInteractions(mapper, sellerApply);
        }

        @Test
        @DisplayName("POST /api/v1/sellers - 성공 시 201 및 Location 반환")
        void applySeller_success() throws Exception {
            Long requesterId = 1L;
            String accountNum = "123-45-67890";
            UUID sellerId = UUID.fromString("00000000-0000-0000-0000-000000000099");
            LocalDateTime now = LocalDateTime.of(2024, 1, 2, 3, 4, 5);

            SellerApplyRequestDto requestDto = new SellerApplyRequestDto(accountNum);
            ApplyCommand command = new ApplyCommand(requesterId, accountNum);
            SellerResult result = new SellerResult(
                sellerId,
                requesterId,
                accountNum,
                SellerStatus.PENDING,
                BigDecimal.ZERO,
                now,
                now,
                null,
                null
            );

            when(mapper.toApplyCommand(requesterId, requestDto)).thenReturn(command);
            when(sellerApply.sellerApply(command)).thenReturn(result);

            String expectedLocation = "/api/v1/sellers/" + sellerId;

            mockMvc.perform(post("/api/v1/sellers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-User-Id", requesterId)
                    .header("X-User-Role", "CONSUMER")
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", expectedLocation))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(URI.create(expectedLocation).toString()))
                .andExpect(jsonPath("$.error").doesNotExist());

            verify(mapper).toApplyCommand(requesterId, requestDto);
            verify(sellerApply).sellerApply(command);
        }
    }
}
