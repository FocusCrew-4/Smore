package com.smore.payment.payment.domain.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("payment_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PgApproveLog {


}

