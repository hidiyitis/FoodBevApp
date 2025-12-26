package com.foodbev.FoodBevApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.foodbev.FoodBevApp.entity")
@EnableJpaRepositories(basePackages = "com.foodbev.FoodBevApp.repository")
public class FoodBevAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodBevAppApplication.class, args);
	}

}
