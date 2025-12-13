package com.smore.seller.application.command;

import java.math.BigDecimal;

public record SettlementRequestCommand(
    Long requesterId,
    BigDecimal amount,
    String role
) {

}
