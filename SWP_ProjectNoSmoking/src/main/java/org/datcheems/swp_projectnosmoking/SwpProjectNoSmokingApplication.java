package org.datcheems.swp_projectnosmoking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class SwpProjectNoSmokingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwpProjectNoSmokingApplication.class, args);
	}

}
