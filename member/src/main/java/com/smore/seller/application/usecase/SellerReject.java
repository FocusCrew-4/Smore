package com.smore.seller.application.usecase;

import java.util.UUID;

public interface SellerReject {

    void rejectSeller(Long id);

    void rejectSeller(UUID uuid);
}
