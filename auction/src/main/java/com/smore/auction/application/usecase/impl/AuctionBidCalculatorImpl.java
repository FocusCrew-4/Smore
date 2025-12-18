package com.smore.auction.application.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.exception.AppErrorCode;
import com.smore.auction.application.exception.AppException;
import com.smore.auction.application.port.out.AuctionBidLogger;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionBidCalculatorImpl implements AuctionBidCalculator {

    private final StringRedisTemplate redis;
    private final RedisKeyFactory key;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final AuctionBidLogger bidLogger;

    // TODO(완료): 한 경매에 관해서 입찰한 내역이 중복으로 계속 들어가는 상황 발생 -> 이렇게 해서 다중수량을 구매하게 가능 quantity 필드는 삭제
    // TODO: 유저 메타데이터 따로 두고 zset 에는 userId 에 관련된 score 만 저장 이후 다중수량 구매는 db 에 quantity 1개씩 펼쳐서 중복레코드 생성해서 각 1개별 별도 구매단위로 처리
    // ex) 수량3개 구매시 zset 에는 userid : 300000 저장해서 계산 userId 메타에는 수량정보 기록 -> 경매 종료후 quantity : 1 로 해서 db 레코드 3개를 만들어 기본순위 랭크 rank, rank + 1, rank + 2 처리하여 별도 주문으로 처리
    // 위의 방식대로하면 WINNER 재선정 코드에도 변화가 없을 것으로 예상됨
    // hset -> auctionId -> auction meta 로 기본시작 경매금 및 minStep 등 관리
    @Override
    public AuctionBidCalculateResult calculateBid(BigDecimal OriginBidPrice, Integer quantity, String auctionId, String userId) {
        // --- 입력 스케일 고정 및 저장
        BigDecimal bidPrice =
            OriginBidPrice.setScale(2, RoundingMode.HALF_UP);
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

        Object value =
            redis.opsForHash()
                .get(key.auctionOpen(auctionId), "minPrice");

        if (value == null) {
            throw new IllegalStateException("minPrice not found");
        }

        BigDecimal minPrice =
            new BigDecimal(value.toString());

        if (bidPrice.compareTo(minPrice) < 0) {
            throw new AppException(AppErrorCode.BID_PRICE_BELOW_MINIMUM);
        }

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
        try {
            bidLogger.writeBidLog(
                UUID.fromString(auctionId),
                Long.valueOf(userId),
                quantity,
                bidPrice.doubleValue()
            );
        } catch (Exception e) {
            log.warn("해당 입찰이 mongoDB 에 로깅되지 않았습니다");
        }
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
        BigDecimal minQualifyingBid = restorePrice(last.getScore());
        if (bidCount < stock) {
            minQualifyingBid = minPrice;
        }

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
