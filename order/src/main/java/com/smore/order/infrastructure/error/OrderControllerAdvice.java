package com.smore.order.infrastructure.error;

import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import com.smore.order.application.exception.CompleteOrderFailException;
import com.smore.order.application.exception.OrderIdMisMatchException;
import com.smore.order.application.exception.RefundReservationConflictException;
import com.smore.order.infrastructure.persistence.exception.CreateOrderFailException;
import com.smore.order.infrastructure.persistence.exception.CreateOutboxFailException;
import com.smore.order.infrastructure.persistence.exception.NotFoundOrderException;
import com.smore.order.infrastructure.persistence.exception.NotFoundOutboxException;
import com.smore.order.infrastructure.persistence.exception.NotFoundRefundException;
import com.smore.order.infrastructure.persistence.exception.UpdateOrderFailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "OrderControllerAdvice")
@RestControllerAdvice
public class OrderControllerAdvice {

    @ExceptionHandler({
        NotFoundOrderException.class,
        NotFoundOutboxException.class,
        NotFoundRefundException.class
    })
    public ResponseEntity<CommonResponse<?>> handleNotFoundException(OrderException ex) {
        log.error("NotFoundException {} - {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getErrorCode()));
    }


    @ExceptionHandler({
        CreateOrderFailException.class,
        CreateOutboxFailException.class,
        CompleteOrderFailException.class,
        OrderIdMisMatchException.class,
        RefundReservationConflictException.class,
        UpdateOrderFailException.class
    })
    public ResponseEntity<CommonResponse<?>> handleConflictException(OrderException ex) {
        log.error("ConflictException {} - {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(ex.getErrorCode()));
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<CommonResponse<?>> handlerOrderException(OrderException ex) {
        log.error("OrderException {} - {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(ex.getErrorCode()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handlerIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("5400", "유효하지 않은 인자를 입력하셨습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handlerException(Exception ex) {
        log.error("Exception {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("5400", "서버에서 예기치 못한 예외가 발생했습니다."));
    }
}



