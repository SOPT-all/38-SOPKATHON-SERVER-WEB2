package com.sopt.sopkathon_web2_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SopkathonWeb2ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SopkathonWeb2ServerApplication.class, args);
	}

}
