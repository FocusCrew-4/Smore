package com.smore.order.application.command;

import com.smore.order.domain.status.OutboxResult;

public interface OutboxHandler {

    OutboxResult execute();

}
