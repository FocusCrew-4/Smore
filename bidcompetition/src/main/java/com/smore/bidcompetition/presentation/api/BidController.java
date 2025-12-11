package com.smore.bidcompetition.presentation.api;

import com.smore.bidcompetition.presentation.dto.BidRequest;
import com.smore.common.response.CommonResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface BidController {

    ResponseEntity<CommonResponse<?>> requestOrder(
        @NotNull @RequestHeader("X-User-Id") Long requesterId,
        @NotBlank @RequestHeader("X-User-Role") String role,
        @Valid @RequestBody BidRequest request
    );

}
