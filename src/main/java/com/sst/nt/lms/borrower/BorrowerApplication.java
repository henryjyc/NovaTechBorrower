package com.sst.nt.lms.borrower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:database-config.properties")
@SpringBootApplication
public class BorrowerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BorrowerApplication.class, args);
	}

}
