package com.smore.bidcompetition.application.repository;

import com.smore.bidcompetition.domain.model.Winner;
import java.util.UUID;

public interface WinnerRepository {

    Winner findByIdempotencyKey(UUID idempotencyKey);

    Winner save(Winner winner);
}
