package com.smore.seller.infrastructure.persistence.jpa.repository;

import com.smore.seller.infrastructure.persistence.jpa.entity.SellerOutbox;
import org.springframework.data.repository.CrudRepository;

public interface SellerOutboxRepository extends CrudRepository<SellerOutbox, Long> {

}
