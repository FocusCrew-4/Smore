//package com.smore.gateway.filter;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class WhiteListFilter implements GlobalFilter, Ordered {
//
//    /*
//    화이트 리스트에 있는 경로는 바로 다음 필터로 넘기고
//    없는 경우는 jwt 존재여부만 검사후 넘김
//     */
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        return null;
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
