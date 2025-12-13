package com.smore.seller.application.usecase;

import com.smore.seller.application.command.SettlementRequestCommand;
import jakarta.ws.rs.core.NoContentException;

public interface RequestSettlement {

    void sendSettlementRequest(SettlementRequestCommand command) throws NoContentException;
}
