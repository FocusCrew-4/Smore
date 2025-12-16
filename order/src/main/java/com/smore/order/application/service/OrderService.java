package com.smore.order.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.order.application.dto.CompletedPaymentCommand;
import com.smore.order.application.dto.CompletedRefundCommand;
import com.smore.order.application.dto.CreateOrderCommand;
import com.smore.order.application.dto.FailedOrderCommand;
import com.smore.order.application.dto.FailedRefundCommand;
import com.smore.order.application.dto.ModifyOrderCommand;
import com.smore.order.application.dto.RefundCommand;
import com.smore.order.application.event.outbound.OrderFailedEvent;
import com.smore.order.application.exception.OrderIdMisMatchException;
import com.smore.order.application.exception.RefundReservationConflictException;
import com.smore.order.application.repository.OrderRepository;
import com.smore.order.application.repository.OutboxRepository;
import com.smore.order.application.repository.RefundRepository;
import com.smore.order.application.event.outbound.OrderCompletedEvent;
import com.smore.order.application.event.outbound.OrderCreatedEvent;
import com.smore.order.application.event.outbound.OrderEvent;
import com.smore.order.application.event.outbound.OrderRefundFailedEvent;
import com.smore.order.application.event.outbound.OrderRefundRequestEvent;
import com.smore.order.application.event.outbound.OrderRefundSucceededEvent;
import com.smore.order.domain.model.Order;
import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.model.Refund;
import com.smore.order.domain.status.AggregateType;
import com.smore.order.domain.status.EventType;
import com.smore.order.domain.status.OrderStatus;
import com.smore.order.domain.status.RefundStatus;
import com.smore.order.domain.status.ServiceResult;
import com.smore.order.domain.vo.Address;
import com.smore.order.application.exception.CompleteOrderFailException;
import com.smore.order.infrastructure.error.OrderErrorCode;
import com.smore.order.infrastructure.persistence.exception.NotFoundOrderException;
import com.smore.order.infrastructure.persistence.exception.UpdateOrderFailException;
import com.smore.order.presentation.dto.DeleteOrderResponse;
import com.smore.order.presentation.dto.IsOrderCreatedResponse;
import com.smore.order.presentation.dto.ModifyOrderResponse;
import com.smore.order.presentation.dto.OrderInfo;
import com.smore.order.presentation.dto.RefundResponse;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j(topic = "OrderService")
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final RefundRepository refundRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    private static final Set<String> ALLOWED_SORTS = Set.of(
        "createdAt",
        "totalAmount",
        "orderedAt"
    );

    @Transactional
    public void createOrder(CreateOrderCommand command) {

        Order findOrder = orderRepository.findByIdempotencyKey(command.getIdempotencyKey());
        if (findOrder != null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now(clock);

        Order createOrder = Order.create(
            command.getUserId(),
            command.getProductId(),
            command.getProductPrice(),
            command.getQuantity(),
            command.getCategoryId(),
            command.getSaleType(),
            command.getIdempotencyKey(),
            now,
            command.getStreet(),
            command.getCity(),
            command.getZipcode()
        );
        Order saveOrder = orderRepository.save(createOrder);

        OrderCreatedEvent event = OrderCreatedEvent.of(
            saveOrder.getId(),
            saveOrder.getUserId(),
            saveOrder.getTotalAmount(),
            saveOrder.getCategoryId(),
            saveOrder.getSaleType(),
            command.getSellerId(),
            UUID.randomUUID(),
            now,
            command.getExpiresAt()
        );

        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            saveOrder.getId(),
            EventType.ORDER_CREATED,
            UUID.randomUUID(),
            makePayload(event)
        );

        outboxRepository.save(outbox);
    }

    @Transactional
    public ServiceResult completeOrder(CompletedPaymentCommand command) {

        UUID orderId = command.getOrderId();
        UUID paymentId = command.getPaymentId();

        Order order = orderRepository.findById(orderId);

        if (order.isCompleted()) {
            log.info("이미 처리된 작업 orderId : {}", orderId);
            return ServiceResult.SUCCESS;
        }

        int updated = orderRepository.completePayment(orderId, paymentId);

        if (updated == 0) {
            log.error("주문 완료 상태로 변경하지 못했습니다. orderId = {}, methodName = {}", orderId, "completeOrder");
            throw new CompleteOrderFailException(OrderErrorCode.COMPLETE_ORDER_CONFLICT);
        }

        OrderCompletedEvent event = OrderCompletedEvent.of(
            order.getId(),
            order.getUserId(),
            OrderStatus.COMPLETED,
            order.getIdempotencyKey(),
            command.getApprovedAt()
        );

        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            order.getId(),
            EventType.ORDER_COMPLETED,
            UUID.randomUUID(),
            makePayload(event)
        );

        outboxRepository.save(outbox);

        return ServiceResult.SUCCESS;
    }

    public IsOrderCreatedResponse isOrderCreated(UUID allocationKey, Long userId) {

        Optional<Order> order = orderRepository.findByAllocationKeyAndUserId(
            allocationKey, userId);
        if (order.isEmpty()) {
            return IsOrderCreatedResponse.notFoundOrder();
        }

        return IsOrderCreatedResponse.from(order.get());
    }

    @Transactional
    public RefundResponse refund(RefundCommand command) {

        Refund refund = refundRepository.findByIdempotencyKey(command.getIdempotencyKey());

        if (refund != null) {
            return RefundResponse.duplication(
                command.getOrderId(),
                "이미 처리된 작업입니다."
            );
        }

        Order order = orderRepository.findById(command.getOrderId());

        if (order.noRefund()) {
            return RefundResponse.fail(
                order.getId(),
                command.getRefundQuantity(),
                command.calculateRefundAmount(order.getProduct().productPrice()),
                order.getRefundReservedQuantity(),
                order.getRefundedQuantity(),
                "환불 요청이 환불 조건에 맞지 않습니다."
            );
        }


        int updated = orderRepository.updateRefundReservation(
            command.getOrderId(),
            command.getUserId(),
            command.getRefundQuantity(),
            order.getRefundReservedQuantity(),
            order.getRefundedQuantity(),
            OrderStatus.REFUNDABLE_STATES
        );

        if (updated == 0) {
            log.error("환불 예약에 실패 orderid : {}, method : {} ", command.getOrderId(), "refund()");
            throw new RefundReservationConflictException(
                OrderErrorCode.REFUND_RESERVATION_CONFLICT
            );
        }

        Refund newRefund = Refund.create(
            command.getOrderId(),
            command.getUserId(),
            order.getProduct().productId(),
            order.getProduct().productPrice(),
            order.getPaymentId(),
            command.getRefundQuantity(),
            command.getIdempotencyKey(),
            command.getReason(),
            command.getType(),
            LocalDateTime.now(clock)
        );

        Refund savedRefund = refundRepository.save(newRefund);

        OrderRefundRequestEvent event = OrderRefundRequestEvent.of(
            savedRefund.getOrderId(),
            order.getUserId(),
            savedRefund.getId(),
            savedRefund.getPaymentId(),
            savedRefund.getRefundAmount(),
            savedRefund.getIdempotencyKey(),
            command.getReason(),
            LocalDateTime.now(clock)
        );

        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            event.getOrderId(),
            EventType.REFUND_REQUEST,
            UUID.randomUUID(),
            makePayload(event)
        );

        outboxRepository.save(outbox);

        Order refresh = orderRepository.findById(command.getOrderId());

        return RefundResponse.success(
            savedRefund.getOrderId(),
            savedRefund.getId(),
            command.getRefundQuantity(),
            savedRefund.getRefundAmount(),
            savedRefund.getStatus(),
            refresh.getRefundReservedQuantity(),
            refresh.getRefundedQuantity(),
            savedRefund.getRequestedAt(),
            "환불 접수에 성공했습니다."
        );
    }


    @Transactional
    public void refundSuccess(CompletedRefundCommand command) {

        Refund refund = refundRepository.findById(command.getRefundId());

        if (refund.isCompleted()) {
            return;
        }

        if (refund.notEqualOrderId(command.getOrderId())) {
            log.error("refund의 orderId와 이벤트의 command의 orderId가 일치하지 않습니다.");
            throw new OrderIdMisMatchException(
                OrderErrorCode.ORDER_ID_MISMATCH
            );
        }

        int updated = refundRepository.complete(
            refund.getId(),
            RefundStatus.COMPLETED,
            LocalDateTime.now(clock)
        );

        if (updated == 0) {
            log.error("환불 완료 실패 orderid : {}, method : {} ", command.getOrderId(), "refundSuccess()");
            throw new RefundReservationConflictException(
                OrderErrorCode.REFUND_RESERVATION_CONFLICT
            );
        }

        Order order = orderRepository.findById(command.getOrderId());

        OrderStatus status = order.calculateStatusAfterRefund(refund.getRefundQuantity());

        updated = orderRepository.applyRefundCompletion(
            command.getOrderId(),
            refund.getRefundQuantity(),
            order.getRefundReservedQuantity(),
            order.getRefundedQuantity(),
            command.getRefundAmount(),
            status
        );

        if (updated == 0) {
            log.error("환불 완료 실패 orderid : {}, method : {} ", command.getOrderId(), "refundSuccess()");
            throw new RefundReservationConflictException(
                OrderErrorCode.REFUND_RESERVATION_CONFLICT
            );
        }

        if (refund.isUserRequest()) {
            OrderRefundSucceededEvent event = OrderRefundSucceededEvent.of(
                refund.getOrderId(),
                refund.getId(),
                order.getUserId(),
                refund.getRefundQuantity(),
                order.getIdempotencyKey(),
                command.getRefundAmount(),
                status,
                LocalDateTime.now(clock)
            );

            Outbox outbox = Outbox.create(
                AggregateType.ORDER,
                command.getOrderId(),
                EventType.REFUND_SUCCESS,
                UUID.randomUUID(),
                makePayload(event)
            );

            outboxRepository.save(outbox);
        }
    }

    @Transactional
    public void refundFail(FailedRefundCommand command) {

        Refund refund = refundRepository.findById(command.getRefundId());

        if (refund.isCompleted()) {
            return;
        }

        if (refund.notEqualOrderId(command.getOrderId())) {
            log.error("refund의 orderId와 이벤트의 command의 orderId가 일치하지 않습니다.");
            throw new OrderIdMisMatchException(
                OrderErrorCode.ORDER_ID_MISMATCH
            );
        }

        int updated = refundRepository.fail(
            refund.getId(),
            RefundStatus.FAILED,
            command.getMessage(),
            LocalDateTime.now(clock)
        );

        if (updated == 0) {
            log.error("환불 요청 실패 기록 하지 못함 orderid : {}, method : {} ", command.getOrderId(), "refundFail()");
            throw new RefundReservationConflictException(
                OrderErrorCode.REFUND_RESERVATION_CONFLICT
            );
        }

        Order order = orderRepository.findById(command.getOrderId());

        updated = orderRepository.refundFail(
            command.getOrderId(),
            refund.getRefundQuantity(),
            order.getRefundReservedQuantity(),
            order.getRefundedQuantity()
        );

        if (updated == 0) {
            log.error("환불 요청 실패 기록 하지 못함 orderid : {}, method : {} ", command.getOrderId(), "refundFail()");
            throw new RefundReservationConflictException(
                OrderErrorCode.REFUND_RESERVATION_CONFLICT
            );
        }

        OrderRefundFailedEvent event = OrderRefundFailedEvent.of(
            command.getOrderId(),
            refund.getId(),
            refund.getUserId(),
            command.getMessage(),
            LocalDateTime.now(clock)
        );

        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            command.getOrderId(),
            EventType.REFUND_FAIL,
            UUID.randomUUID(),
            makePayload(event)
        );

        outboxRepository.save(outbox);
    }

    @Transactional
    public ModifyOrderResponse modify(ModifyOrderCommand command) {

        Order order = orderRepository.findById(command.getOrderId());

        if (order.notEqualUserId(command.getUserId())) {
            return ModifyOrderResponse.fail(
                command.getOrderId(),
                "주문자와 주문 수정자가 일치하지 않습니다."
            );
        }

        if (order.equalAddress(command.getAddress())) {
            return ModifyOrderResponse.success(
                order.getId(),
                order.getAddress().street(),
                order.getAddress().city(),
                order.getAddress().zipcode(),
                order.getAddress().street(),
                order.getAddress().city(),
                order.getAddress().zipcode()
            );
        }

        Address beforeAddress = order.getAddress();

        order.changeAddressInfo(
            command.getAddress().street(),
            command.getAddress().city(),
            command.getAddress().zipcode()
        );

        int updated = orderRepository.update(order);

        if (updated == 0) {
            log.error("주문 정보 수정 실패 orderid : {}, method : {} ", command.getOrderId(), "modify()");
            throw new UpdateOrderFailException(OrderErrorCode.UPDATE_ORDER_FAIL_CONFLICT);
        }

        return ModifyOrderResponse.success(
            order.getId(),
            beforeAddress.street(),
            beforeAddress.city(),
            beforeAddress.zipcode(),
            command.getAddress().street(),
            command.getAddress().city(),
            command.getAddress().zipcode()
        );
    }

    @Transactional
    public DeleteOrderResponse delete(UUID orderId, Long userId) {

        Optional<Order> content = orderRepository.findByIdIncludingDeleted(orderId);

        if (content.isEmpty()) {
            log.error("주문을 찾을 수 없습니다. orderId : {}", orderId);
            throw new NotFoundOrderException(OrderErrorCode.NOT_FOUND_ORDER);

        }

        Order order = content.get();

        if (order.notEqualUserId(userId)) {
            return DeleteOrderResponse.fail(
                "주문자와 주문 삭제자가 일치하지 않습니다."
            );
        }

        if (order.isDeleted()) {
            return DeleteOrderResponse.success(
                "이미 삭제된 주문입니다.",
                order.getDeletedAt(),
                order.getDeletedBy()
            );
        }

        if (order.isUndeletable()) {
            return DeleteOrderResponse.fail(
                "삭제 가능한 상태가 아닙니다."
            );
        }

        LocalDateTime now = LocalDateTime.now(clock);
        int updated = orderRepository.delete(
            orderId,
            userId,
            now
        );

        if (updated == 0) {
            log.error("동시 처리로 주문 삭제 실패 orderid : {}, method : {} ", orderId, "delete()");
            throw new UpdateOrderFailException(OrderErrorCode.UPDATE_ORDER_FAIL_CONFLICT);

        }

        return DeleteOrderResponse.success(
            "주문 삭제에 성공했습니다.",
            now,
            userId
        );
    }

    public OrderInfo searchOrderOne(UUID orderId) {

        Order order = orderRepository.findById(orderId);

        return OrderInfo.of(order);
    }

    public Page<OrderInfo> searchOrderList(Long userId, UUID productId, Pageable pageable) {

        validateSort(pageable);

        Page<Order> orderPage = orderRepository.findAll(userId, productId, pageable);

        return orderPage.map(OrderInfo::of);
    }

    private Pageable validateSort(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!ALLOWED_SORTS.contains(order.getProperty())) {
                throw new IllegalArgumentException("정렬 불가 필드: " + order.getProperty());
            }
        }
        return pageable;
    }

    @Transactional
    public void failOrder(FailedOrderCommand command) {

        Order order = orderRepository.findById(command.getOrderId());

        if (order.isFailed()) {
            log.info("이미 처리된 작업 orderId : {}", command.getOrderId());
            return;
        }

        int updated = orderRepository.fail(
            command.getOrderId(),
            order.getOrderStatus()
        );

        if (updated == 0) {
            log.error("주문 실패 실패 orderid : {}, method : {} ", command.getOrderId(), "failOrder()");
            throw new UpdateOrderFailException(OrderErrorCode.UPDATE_ORDER_FAIL_CONFLICT);
        }

        OrderFailedEvent event = OrderFailedEvent.of(
            order.getIdempotencyKey(),
            order.getProduct().productId(),
            order.getUserId(),
            order.getQuantity(),
            LocalDateTime.now(clock)
        );

        Outbox outbox = Outbox.create(
            AggregateType.ORDER,
            order.getId(),
            EventType.ORDER_FAILED,
            UUID.randomUUID(),
            makePayload(event)
        );

        outboxRepository.save(outbox);

    }

    // TODO: 나중에 클래스로 분리할 예정
    private String makePayload(OrderEvent event)  {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("이벤트 Payload JSON 변환 실패");
            throw new RuntimeException(e);
        }
    }

}
