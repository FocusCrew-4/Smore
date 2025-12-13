package com.smore.seller.application.port;

public interface SellerTopicPort {
    String getSellerRegisterTopic(String version);
    String getSellerSettlementTopic(String version);
}
