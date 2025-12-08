package com.smore.seller.presentation.web.mapper;

import com.smore.seller.application.command.ApplyCommand;
import com.smore.seller.presentation.web.dto.request.SellerApplyRequestDto;
import org.springframework.stereotype.Component;

@Component
public class SellerControllerMapper {

    public ApplyCommand toApplyCommand(Long requesterId, SellerApplyRequestDto requestDto) {
        return new ApplyCommand(
            requesterId,
            requestDto.accountNum()
        );
    }
}
