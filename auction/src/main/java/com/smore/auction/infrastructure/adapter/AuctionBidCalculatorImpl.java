package com.smore.auction.infrastructure.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.result.AuctionBidCalculateResult;
import com.smore.auction.application.service.usecase.AuctionBidCalculator;
import com.smore.auction.infrastructure.redis.RedisKeyFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
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

    // TODO: 한 경매에 관해서 입찰한 내역이 중복으로 계속 들어가는 상황 발생
    // zset -> userId, score 로 관리
    // hset -> userId, metaData 로 관리 예정
    // hset -> auctionId -> auction meta 로 기본시작 경매금 및 minStep 등 관리
    @Override
    public AuctionBidCalculateResult calculateBid(BigDecimal bidPrice, Integer quantity, String auctionId, String userId) {
        // --- 입력 스케일 고정 및 저장
        bidPrice = bidPrice.setScale(2, RoundingMode.HALF_UP);
        var metaData
            = new RedisBidData(userId, quantity, bidPrice, LocalDateTime.now(clock));

        String stringMetaData;
        try {
            stringMetaData = objectMapper.writeValueAsString(metaData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        long scaled = bidPrice
            .movePointRight(2)
            .longValueExact();

        redis.opsForZSet()
            .add(key.auctionBids(auctionId), stringMetaData, scaled);

        // 4. Top10 조회
        Set<TypedTuple<String>> top10 =
            redis.opsForZSet()
                .reverseRangeWithScores(key.auctionBids(auctionId), 0, 9);

        if (top10 == null || top10.isEmpty()) {
            return new AuctionBidCalculateResult(BigDecimal.ZERO, BigDecimal.ZERO, bidPrice, null);
        }

        // 5. 1위 추출
        Iterator<TypedTuple<String>> it = top10.iterator();
        TypedTuple<String> first = it.next();
        BigDecimal highestBid = restorePrice(first.getScore());

        // 6. 10위(또는 마지막 요소) 추출
        TypedTuple<String> last = null;
        for (TypedTuple<String> t : top10) {
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
