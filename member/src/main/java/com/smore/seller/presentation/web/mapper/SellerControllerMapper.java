package com.smore.seller.presentation.web.mapper;

import com.smore.seller.application.command.ApplyCommand;
import com.smore.seller.application.command.SettlementRequestCommand;
import com.smore.seller.presentation.web.dto.SettlementRequestDto;
import com.smore.seller.presentation.web.dto.request.SellerApplyRequestDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class SellerControllerMapper {

    public ApplyCommand toApplyCommand(Long requesterId, SellerApplyRequestDto requestDto) {
        return new ApplyCommand(
            requesterId,
            requestDto.accountNum()
        );
    }

    public SettlementRequestCommand toSettlementRequestCommand(Long requesterId, String role, @Valid SettlementRequestDto requestDto) {
        return new SettlementRequestCommand(
            requesterId,
            requestDto.reqAmount(),
            role
        );
    }
}
