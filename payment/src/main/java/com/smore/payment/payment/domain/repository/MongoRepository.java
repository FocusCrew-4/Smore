package com.smore.payment.payment.domain.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface MongoRepository {
    void save();
}
