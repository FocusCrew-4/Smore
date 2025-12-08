package com.smore.auction.infrastructure.websocket;

import java.security.Principal;

/*
Spring WebSocket 구조에서 사용하는 기능들이 java.security.Principal 기반으로 돌아감
 */
public class AuctionUserPrincipal implements Principal {
    private final Long userId;
    private final String role;

    public AuctionUserPrincipal(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    public String getRole() {
        return role;
    }
}
