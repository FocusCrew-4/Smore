package com.smore.auction.infrastructure.adapter;

import com.smore.auction.application.command.AuctionStartCommand;
import com.smore.auction.application.service.usecase.AuctionStart;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.model.Auction;
import com.smore.auction.infrastructure.redis.RedisKeyFactory;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NoContentException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionStartImpl implements AuctionStart {

    private final AuctionSqlRepository auctionRepository;
    private final RedisKeyFactory key;
    private final StringRedisTemplate redis;

    // 경매시작 비즈니스 로직
    @Override
    public void start(AuctionStartCommand command) throws NoContentException {

        Auction auction
        = auctionRepository.findByProductId(command.productId());
        if (auction == null || !auction.isReady()) {
            throw new NoContentException("준비중인 경매건이 존재하지 않습니다");
        }
        auction.start();

        // TODO: 추후 handleSubscribe 에서 여기서 등록된 auction 이 아니면 토픽생성이 안 되게 변경
        redis.opsForValue()
            .set(key.auctionOpen(String.valueOf(auction.getId())), String.valueOf(command.idempotencyKey()));

        auctionRepository.save(auction);
    }
}
