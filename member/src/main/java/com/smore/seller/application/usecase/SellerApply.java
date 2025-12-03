package com.smore.seller.application.usecase;

import com.smore.seller.application.command.ApplyCommand;
import com.smore.seller.application.result.SellerResult;

public interface SellerApply {
    SellerResult sellerApply(ApplyCommand applyCommand);
}
