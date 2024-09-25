package com.capstoneproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@SpringBootApplication
@RestController
public class CapstoneprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapstoneprojectApplication.class, args);
	}

	@GetMapping("/")
	public String home() {
		return new String("Welcome to my capstone project");
	}
	@GetMapping("/welcome")
	public String welcome() {
		return new String("Welcome to my capstone project, welcome page");
	}

}
