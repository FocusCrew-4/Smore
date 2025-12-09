package com.smore.bidcompetition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BidcompetitionApplication {

	public static void main(String[] args) {
		SpringApplication.run(BidcompetitionApplication.class, args);
	}

}
