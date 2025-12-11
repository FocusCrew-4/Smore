package com.smore.bidcompetition.application.handler;


import com.smore.bidcompetition.domain.status.OutboxResult;

public interface OutboxHandler {

    OutboxResult execute();

}
