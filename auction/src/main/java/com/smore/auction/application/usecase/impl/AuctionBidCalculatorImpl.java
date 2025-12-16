package com.smore.auction.application.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.exception.AppErrorCode;
import com.smore.auction.application.exception.AppException;
import com.smore.auction.application.result.AuctionBidCalculateResult;
import com.smore.auction.application.usecase.AuctionBidCalculator;
import com.smore.auction.infrastructure.redis.RedisKeyFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionBidCalculatorImpl implements AuctionBidCalculator {

    private final StringRedisTemplate redis;
    private final RedisKeyFactory key;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    // TODO: 한 경매에 관해서 입찰한 내역이 중복으로 계속 들어가는 상황 발생 -> 이렇게 해서 다중수량을 구매하게 가능 quantity 필드는 삭제
    // hset -> auctionId -> auction meta 로 기본시작 경매금 및 minStep 등 관리
    @Override
    public AuctionBidCalculateResult calculateBid(BigDecimal bidPrice, Integer quantity, String auctionId, String userId) {
        // --- 입력 스케일 고정 및 저장
        bidPrice = bidPrice.setScale(2, RoundingMode.HALF_UP);
        var metaData =
            new RedisBidData(userId, quantity, bidPrice, LocalDateTime.now(clock));

        String stockValue = redis.opsForHash()
                .get(key.auctionOpen(auctionId), "stock").toString();
        Objects.requireNonNull(stockValue, "auction open stock is null");
        Long stock = Long.valueOf(stockValue);

        String stringMetaData;
        try {
            stringMetaData = objectMapper.writeValueAsString(metaData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        long scaledBid = bidPrice
            .movePointRight(2)
            .longValueExact();

        Long bidCount =
            redis.opsForZSet()
                .zCard(key.auctionBids(auctionId));

        if (bidCount >= stock) {
            // 컷오프 존재
            Set<TypedTuple<String>> cutoff =
                redis.opsForZSet()
                    .reverseRangeWithScores(
                        key.auctionBids(auctionId),
                        stock - 1,
                        stock - 1
                    );

            TypedTuple<String> last =
                cutoff.iterator().next();

            long minQualifyingScaled =
                BigDecimal.valueOf(last.getScore())
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValueExact();

            if (scaledBid <= minQualifyingScaled) {
                throw new AppException(AppErrorCode.BID_PRICE_BELOW_MINIMUM);
            }
        }

        redis.opsForZSet()
            .add(key.auctionBids(auctionId), stringMetaData, scaledBid);

        Set<TypedTuple<String>> candidate =
            redis.opsForZSet()
                .reverseRangeWithScores(key.auctionBids(auctionId), 0, stock - 1);

        if (candidate == null || candidate.isEmpty()) {
            return new AuctionBidCalculateResult(BigDecimal.ZERO, BigDecimal.ZERO, bidPrice, null);
        }

        // 5. 1위 추출
        Iterator<TypedTuple<String>> it = candidate.iterator();
        TypedTuple<String> first = it.next();
        BigDecimal highestBid = restorePrice(first.getScore());

        // 6. 마지막 요소 추출
        TypedTuple<String> last = null;
        for (TypedTuple<String> t : candidate) {
            last = t;
        }

        BigDecimal minQualifyingBid =
            restorePrice(last.getScore());

        // 7. 반환
        return new AuctionBidCalculateResult(
            highestBid,
            minQualifyingBid,
            bidPrice,
            null
        );
    }

    private BigDecimal restorePrice(Double score) {
        long scaled = BigDecimal.valueOf(score)
            .setScale(0, RoundingMode.HALF_UP)
            .longValue();
        return BigDecimal.valueOf(scaled, 2);
    }
    public record RedisBidData(
        String userId,
        Integer quantity,
        BigDecimal bidPrice,
        LocalDateTime createdAt
    ) {

    }
}
