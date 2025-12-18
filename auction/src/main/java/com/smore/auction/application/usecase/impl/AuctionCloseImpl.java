package com.smore.auction.application.usecase.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.usecase.AuctionClose;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.model.Auction;
import com.smore.auction.domain.model.AuctionBidderRank;
import com.smore.auction.application.usecase.impl.AuctionBidCalculatorImpl.RedisBidData;
import com.smore.auction.infrastructure.redis.RedisKeyFactory;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionCloseImpl implements AuctionClose {

    private final AuctionSqlRepository auctionSqlRepository;
    private final StringRedisTemplate redis;
    private final RedisKeyFactory key;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    /*
    1. auctionId 로 auction 객체 가져온다
    2. auctionId key 의 zset 을 가져온다
    3. zset 값의 메타데이터를 읽어 auctionBidderRank 로 변환
    4. auctionBidderRank List 를 save
    5. auctionId 의 Redis key 전부 삭제
    6. 상품쪽에 이벤트 발행?
     */
    // TODO: 멀티서버시 SETNX close-lock 활용 해서 동시성 제어
    // TODO: session 이 참여중인 auction 도 잡아서 지워야함
    @Override
    public void close(String auctionId) {
        Auction auction
            = auctionSqlRepository.findById(auctionId);
        if (auction == null || auction.isClosed()) return;

        var tuples = redis.opsForZSet()
            .reverseRangeWithScores(key.auctionBids(auctionId), 0, -1);

        if (tuples == null || tuples.isEmpty()) {
            deleteAuctionKeys(auctionId);
            auction.close();
            auctionSqlRepository.save(auction);
            return;
        }

        List<AuctionBidderRank> ranks = mapToRanks(auction, tuples);
        auctionSqlRepository.saveAll(ranks);

        auction.close();
        auctionSqlRepository.save(auction);

        deleteAuctionKeys(auctionId);
    }

    private void deleteAuctionKeys(String auctionId) {
        List<String> keyToDelete = new ArrayList<>();

        keyToDelete.add(key.auctionBids(auctionId));
        keyToDelete.add(key.auctionSessions(auctionId));
        keyToDelete.add(key.auctionOpen(auctionId));

        redis.delete(keyToDelete);
    }

    private List<AuctionBidderRank> mapToRanks(Auction auction, Set<TypedTuple<String>> tuples) {
        AtomicLong rankCounter = new AtomicLong(1);

        List<AuctionBidderRank> resultList
            = tuples.stream()
                .map(tuple -> {
                    try {
                        RedisBidData data
                            = objectMapper.readValue(tuple.getValue(), RedisBidData.class);

                        long rank = rankCounter.getAndIncrement();

                        boolean isWinner = rank <= auction.getStock();

                        if (isWinner) {
                            return AuctionBidderRank.createWinner(
                                auction.getId(),
                                Long.valueOf(data.userId()),
                                data.bidPrice(),
                                data.quantity(),
                                rank,
                                clock
                            );
                        } else {
                            return AuctionBidderRank.createStandBy(
                                auction.getId(),
                                Long.valueOf(data.userId()),
                                data.bidPrice(),
                                data.quantity(),
                                rank,
                                clock
                            );
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid bidder JSON: " + tuple.getValue(), e);
                    }
                })
                .collect(Collectors.toList());

        return resultList;
    }
}
