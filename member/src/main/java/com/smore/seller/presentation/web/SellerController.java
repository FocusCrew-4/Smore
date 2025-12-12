package com.smore.seller.presentation.web;

import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import com.smore.seller.application.result.SellerResult;
import com.smore.seller.application.usecase.RequestSettlement;
import com.smore.seller.application.usecase.SellerApply;
import com.smore.seller.application.usecase.SellerApprove;
import com.smore.seller.application.usecase.SellerReject;
import com.smore.seller.presentation.web.dto.SettlementRequestDto;
import com.smore.seller.presentation.web.dto.request.SellerApplyRequestDto;
import com.smore.seller.presentation.web.mapper.SellerControllerMapper;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerControllerMapper mapper;
    private final SellerApply sellerApply;
    private final SellerReject sellerReject;
    private final SellerApprove sellerApprove;
    private final RequestSettlement requestSettlement;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    // TODO: 예외타입 변경 필요
    @PostMapping
    public ResponseEntity<CommonResponse<?>> applySeller(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") String role,
        @RequestBody @Valid SellerApplyRequestDto requestDto
    ) throws URISyntaxException {
        if (!role.equals("CONSUMER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("S401", "일반회원만 이용 가능합니다"));
        }

        SellerResult sellerResult
            = sellerApply.sellerApply(mapper.toApplyCommand(requesterId, requestDto));

        URI uri = new URI("/api/v1/sellers/" + sellerResult.id());

        return ResponseEntity.created(uri)
            .body(ApiResponse.ok(uri));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<CommonResponse<?>> rejectSeller(
        @RequestHeader("X-User-Role") String role,
        @PathVariable Long id
    ) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("S401", "관리자만 사용가능합니다"));
        }

        sellerReject.rejectSeller(id);

        return ResponseEntity.ok(ApiResponse.ok("거절 성공"));
    }

    // TODO: 이미 승인된 Seller 중복 승인할 경우 예외처리 필요
    @PostMapping("/{id}/approve")
    public ResponseEntity<CommonResponse<?>> approveSeller(
        @RequestHeader("X-User-Role") String role,
        @PathVariable Long id
    ) {
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("S401", "관리자만 사용가능합니다"));
        }

        sellerApprove.approveSeller(id);

        return ResponseEntity.ok(ApiResponse.ok("승인 성공"));
    }

    @PostMapping("/settlements/request")
    public ResponseEntity<CommonResponse<?>> requestSettlement(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") String role,
        @Valid @RequestBody SettlementRequestDto requestDto
    ) {

    }

}
