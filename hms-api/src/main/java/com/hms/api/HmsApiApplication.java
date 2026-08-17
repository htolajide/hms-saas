package com.hms.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = { "com.hms" })
@EntityScan(basePackages = { "com.hms" })
@EnableJpaRepositories(basePackages = { "com.hms" })
public class HmsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HmsApiApplication.class, args);
	}
}