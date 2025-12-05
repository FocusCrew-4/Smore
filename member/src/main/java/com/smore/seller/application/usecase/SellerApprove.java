package com.smore.seller.application.usecase;

import java.util.UUID;

public interface SellerApprove {

    void approveSeller(Long id);

    void approveSeller(UUID uuid);

}
